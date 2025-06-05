package com.example.controller;

import java.security.Principal;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.entites.Contact;
import com.example.entites.User;
import com.example.helper.Message;
import com.example.repository.ContactRepository;
import com.example.repository.UserRepository;
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
	private EmailService emailService;

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
	public String handleAddContact(Contact contact, @RequestParam("profileimage") MultipartFile file,
			Principal principal, HttpSession session) {
		try {
			User user = userRepo.findByUserName(principal.getName());

			if (!file.isEmpty()) {
				contact.setImage(file.getBytes());
			}

			contact.setUser(user);
			user.getContact().add(contact);
			userRepo.save(user);

			session.setAttribute("message", new Message("Your contact is added!", "success"));
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong!", "danger"));
		}

		return "user/addcontact";
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

	@RequestMapping("/delete/{cid}")
	@Transactional
	public String deleteContact(@PathVariable("cid") Integer cid, HttpSession session, Principal principal) {
		Contact contact = contactRepo.findById(cid).orElse(null);

		if (contact != null) {
			User user = userRepo.findByUserName(principal.getName());
			user.getContact().remove(contact);
			userRepo.save(user);
			session.setAttribute("message", new Message("Contact deleted successfully", "success"));
		}

		return "redirect:/user/shows/0";
	}

	@RequestMapping("/updatecontact/{cid}")
	public String showUpdateForm(@PathVariable("cid") Integer cid, Model model) {
		Contact contact = contactRepo.findById(cid).orElse(null);
		model.addAttribute("contact", contact);
		return "user/update";
	}

	@PostMapping("/update")
	public String handleUpdateContact(@ModelAttribute Contact contact, @RequestParam("profileimage") MultipartFile file,
			HttpSession session, Principal principal) {
		try {
			User user = userRepo.findByUserName(principal.getName());
			contact.setUser(user);

			if (!file.isEmpty()) {
				contact.setImage(file.getBytes());
			}

			contactRepo.save(contact);
			session.setAttribute("message", new Message("Contact updated successfully", "success"));
		} catch (Exception e) {
			session.setAttribute("message", new Message("Update failed", "danger"));
			e.printStackTrace();
		}

		return "redirect:/user/shows/0";
	}

	@GetMapping("/settingpage")
	public String openSettingsPage() {
		return "user/settings";
	}

	@PostMapping("/changepass")
	public String changePassword(@RequestParam("oldpass") String oldPass, @RequestParam("newpass") String newPass,
			HttpSession session, Principal principal) {
		User user = userRepo.findByUserName(principal.getName());

		if (passwordencoder.matches(oldPass, user.getPassword())) {
			user.setPassword(passwordencoder.encode(newPass));
			userRepo.save(user);
			session.setAttribute("message", new Message("Password changed successfully", "success"));
		} else {
			session.setAttribute("message", new Message("Incorrect old password", "danger"));
			return "user/settings";
		}

		return "redirect:/user/index";
	}

	@PostMapping("/update-profile")
	public String updateUserProfile(@RequestParam("name") String name, @RequestParam("email") String email,
			Principal principal, HttpSession session) {
		try {
			User user = userRepo.findByUserName(principal.getName());
			user.setName(name);
			user.setEmail(email);
			userRepo.save(user);
			session.setAttribute("message", new Message("Profile updated successfully", "success"));
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("Failed to update profile", "danger"));
		}

		return "redirect:/user/settingpage";
	}

	@GetMapping("/feedback")
	public String feedbackForm(Model model) {
		model.addAttribute("title", "Feedback");
		return "user/feedback_form";
	}

	@PostMapping("/submit-feedback")
	public String submitFeedback(@RequestParam("subject") String subject, @RequestParam("email") String email,
			@RequestParam("message") String message, HttpSession session) {
		try {
			emailService.sendFeedbackEmail(subject, email, message);
			session.setAttribute("message", new Message("Thank you for your feedback!", "alert-success"));
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("Feedback sending failed!", "alert-danger"));
		}

		return "redirect:/user/feedback";
	}

	@GetMapping("/user/profile")
	public String showProfile(Model model, Principal principal) {
		User user = userRepo.findByEmail(principal.getName());

		if (user.getImage() != null) {
			String base64Image = Base64.getEncoder().encodeToString(user.getImage());
			model.addAttribute("imageBase64", base64Image);
		} else {
			model.addAttribute("imageBase64", null);
		}

		model.addAttribute("user", user);
		return "user/profile";
	}

	@GetMapping("/premium")
	public String pirmiumPage(Model model) {
				
		return "user/premium";
}
}
