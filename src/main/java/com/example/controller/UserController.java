package com.example.controller;

import java.security.Principal;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.entites.Contact;
import com.example.entites.User;
import com.example.helper.Message;
import com.example.repository.ContactRepository;
import com.example.repository.UserRepository;
import com.example.services.CloudinaryService;
import com.example.services.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private BCryptPasswordEncoder passwordencoder;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ContactRepository contactRepo;

	@Autowired
	private CloudinaryService cloudinaryService;

	// Common method to add user object to all responses
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		User user = userRepo.findByUserName(userName);
		model.addAttribute("user", user);
	}

	@RequestMapping("/index")
	public String userDashboard(Model model, Principal principal) {
		User user = userRepo.findByUserName(principal.getName());
		model.addAttribute("totalContacts", user.getContact().size());
		model.addAttribute("contacts", user.getContact());
		return "user/userdashboard";
	}

	@RequestMapping("/addcontacts")
	public String showAddContactForm(Model model) {
		model.addAttribute("contact", new Contact());
		return "user/addcontact";
	}

	@PostMapping("/add_contact")
	public String handleAddContact(Contact contact, @RequestParam("profileimage") MultipartFile files,
			Principal principal, RedirectAttributes redirectAttributes) {
		try {
			User user = userRepo.findByUserName(principal.getName());

			if (!files.isEmpty()) {
				String imageUrl = cloudinaryService.uploadFile(files); // ✅ Upload to cloudinary
				contact.setImageUrl(imageUrl); // ✅ Set it to the contact
			} else {
				contact.setImageUrl("/images/default.png"); // Optional: default image
			}

			contact.setUser(user);
			user.getContact().add(contact);
			userRepo.save(user);

			redirectAttributes.addFlashAttribute("message", new Message("Your contact is added!", "success"));
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", new Message("Something went wrong!", "danger"));
		}

		return "redirect:/user/addcontacts";
	}

	@GetMapping("/shows")
	public String redirectToFirstPage() {
		return "redirect:/user/shows/0";
	}

	@GetMapping("/shows/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
		User user = userRepo.findByUserName(principal.getName());

		Pageable pageable = PageRequest.of(page, 4);
		Page<Contact> contacts = contactRepo.findContactByUser(user.getId(), pageable);

		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());

		return "user/showcontact";
	}

	@GetMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		Optional<Contact> optionalContact = contactRepo.findById(cId);
		Contact contact = optionalContact.orElse(null);

		if (contact != null) {
			User user = userRepo.findByUserName(principal.getName());
			if (user.getId() == contact.getUser().getId()) {
				model.addAttribute("contact", contact);
			}
		}

		return "user/contactdetails";
	}

	@GetMapping("/profile")
	public String userProfile() {
		return "user/profile";
	}

	@GetMapping("/settingpage")
	public String openSettingsPage() {
		return "user/settings";
	}

	@RequestMapping("/delete/{cid}")
	@Transactional
	public String deleteContact(@PathVariable("cid") Integer cid, RedirectAttributes redirectAttributes,
			Principal principal) {
		Contact contact = contactRepo.findById(cid).orElse(null);

		if (contact != null) {
			User user = userRepo.findByUserName(principal.getName());
			user.getContact().remove(contact);
			userRepo.save(user);
			redirectAttributes.addFlashAttribute("message", new Message("Contact deleted successfully", "success"));
		}

		return "redirect:/user/shows/0";
	}

	@RequestMapping("/updatecontact/{cid}")
	public String showUpdateForm(@PathVariable("ciId") Integer cId, Model model) {
		Contact contact = contactRepo.findById(cId).orElse(null);
		model.addAttribute("contact", contact);
		return "user/update";
	}

	@PostMapping("/update")
	public String handleUpdateContact(@ModelAttribute Contact updatedContact,
			@RequestParam("profileimage") MultipartFile files, RedirectAttributes redirectAttributes,
			Principal principal) {

		try {
			Contact existingContact = contactRepo.findById(updatedContact.getcId()).orElse(null);

			if (existingContact == null) {
				redirectAttributes.addFlashAttribute("message", new Message("Contact not found", "danger"));
				return "redirect:/user/shows/0";
			}

			User user = userRepo.findByUserName(principal.getName());

			// Check if this contact belongs to the current user
			if (existingContact.getUser().getId() != user.getId()) {
				redirectAttributes.addFlashAttribute("message",
						new Message("You don't have permission to update this contact", "danger"));
				return "redirect:/user/shows/0";
			}

			// Update fields
			existingContact.setUsername(updatedContact.getUsername());
			existingContact.setLastname(updatedContact.getLastname());
			existingContact.setJobTitle(updatedContact.getJobTitle());
			existingContact.setEmail(updatedContact.getEmail());
			existingContact.setPhone(updatedContact.getPhone());
			existingContact.setAddress(updatedContact.getAddress());

			if (!files.isEmpty()) {
				String imageUrl = cloudinaryService.uploadFile(files); // ✅ Upload to cloudinary
				existingContact.setImageUrl(imageUrl); // ✅ Set it to the contact
			} else {
				existingContact.setImageUrl("/images/default.png"); // Optional: default image
			}

			contactRepo.save(existingContact);
			redirectAttributes.addFlashAttribute("message", new Message("Contact updated successfully", "success"));
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", new Message("Update failed", "danger"));
			e.printStackTrace();
		}

		return "redirect:/user/shows/0";
	}

	@PostMapping("/changepass")
	public String changePassword(@RequestParam("oldpass") String oldPass, @RequestParam("newpass") String newPass,
			RedirectAttributes redirectAttributes, Principal principal) {
		User user = userRepo.findByUserName(principal.getName());

		if (passwordencoder.matches(oldPass, user.getPassword())) {
			user.setPassword(passwordencoder.encode(newPass));
			userRepo.save(user);
			redirectAttributes.addFlashAttribute("message", new Message("Password changed successfully", "success"));
		} else {
			redirectAttributes.addFlashAttribute("message", new Message("Incorrect old password", "danger"));
			return "user/settings";
		}

		return "redirect:/user/settingpage";
	}

	@PostMapping("/update-profile")
	public String updateUserProfile(@RequestParam("name") String name, @RequestParam("email") String email,
			@RequestParam("profileImage") MultipartFile file, Principal principal,
			RedirectAttributes redirectAttributes) {
		try {
			User user = userRepo.findByUserName(principal.getName());

			user.setName(name);
			user.setEmail(email);

			// Update image only if a new file is uploaded
			if (!file.isEmpty()) {
				String imageUrl = cloudinaryService.uploadFile(file);
				user.setImageUrl(imageUrl);

			}

			userRepo.save(user);
			redirectAttributes.addFlashAttribute("message", new Message("Profile updated successfully", "success"));
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", new Message("Failed to update profile", "danger"));
		}

		return "redirect:/user/settingpage";
	}

//	@GetMapping("/user/image/{id}")
//	@ResponseBody
//	public ResponseEntity<byte[]> getUserImage(@PathVariable("id") int id) {
//		Optional<User> userOpt = userRepo.findById(id);
//		if (userOpt.isPresent() && userOpt.get().getImage() != null) {
//			byte[] image = userOpt.get().getImage();
//
//			HttpHeaders headers = new HttpHeaders();
//			headers.setContentType(MediaType.IMAGE_JPEG); // or MediaType.IMAGE_PNG
//			return new ResponseEntity<>(image, headers, HttpStatus.OK);
//		} else {
//			return ResponseEntity.notFound().build();
//		}
//	}

	@GetMapping("/premiums")
	public String premium() {
		
		return "user/premium";
	}
	
}
