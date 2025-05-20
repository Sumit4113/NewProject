package com.example.controller;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.entites.Contact;
import com.example.entites.Feedback;
import com.example.entites.User;
import com.example.helper.Message;
import com.example.repository.ContactRepository;
import com.example.repository.FeedbackRepository;
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

	// dashboar home content connectivity
	@ModelAttribute
	public void addCommon(Model model, Principal principal) {

		String userName = principal.getName();
		System.out.println("UserName" + userName);

		// get the user using username(Email)

		User user = userRepo.findByUserName(userName);

		model.addAttribute("user", user);

	}

	// Its a UserDashboard page
	@RequestMapping("/index")
	public String userPage(Model model) {

		return "user/user_dashboard";
	}

//Its user for open addcontact page
	@RequestMapping("/addcontacts")
	public String showContact(Model model) {

		model.addAttribute("contact", new Contact());

		return "user/addcontact";
	}

	// Its user for add data on contact form
	@RequestMapping("/add_contact")
	public String addContact(Contact contact, @RequestParam("profileimage") MultipartFile file, Principal principal,
			HttpSession session) {

		try {
			String userNames = principal.getName();
			User user = userRepo.findByUserName(userNames);

			// Handle file upload
			if (file.isEmpty()) {
				contact.setImage("default.png");
			} else {
				// Save file outside the project, e.g. in "contact-photos" folder
				String uploadDir = "contact-photos";
				File folder = new File(uploadDir);
				if (!folder.exists())
					folder.mkdirs();

				String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
				Path path = Paths.get(uploadDir, fileName);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				contact.setImage(fileName);
			}

			contact.setUser(user);
			user.getContact().add(contact);
			this.userRepo.save(user);

			session.setAttribute("message", new Message("Your contact is added !! Add new", "success"));

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong!! Try again", "danger"));
		}

		return "user/addcontact";
	}

//show contact handler
	// page = 4[n]
	@GetMapping("/shows")
	public String defaultShowRedirect() {
		return "redirect:/user/shows/0";
	}

	@GetMapping("/shows/{page}")
	public String showContact(@PathVariable("page") Integer page, Model model, Principal principal) {
		String userName = principal.getName();
		User userid = this.userRepo.findByUserName(userName);

		Pageable pageable = PageRequest.of(page, 4);
		Page<Contact> contacts = this.contactRepo.findContactByUser(userid.getId(), pageable);
		model.addAttribute("currentPage", page != null ? page : 0);
		model.addAttribute("totalPages", contacts != null ? contacts.getTotalPages() : 1);
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		System.out.println("Total Pages: " + contacts.getTotalPages());

		return "user/showcontact"; // Must match the Thymeleaf template filename (showcontact.html)
	}

	// show particular detail

	@RequestMapping("/{cId}/contact/")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {

		System.out.println("cid" + cId);

		Optional<Contact> opcontact = this.contactRepo.findById(cId);

		Contact contact = opcontact.get();

		String username = principal.getName();
		User user = this.userRepo.findByUserName(username);

		if (user.getId() == contact.getUser().getId()) {

			model.addAttribute("contact", contact);

		}
		return "user/contactdetails";
	}

	@GetMapping("/profile")
	public String userProfile(Model model) {

		return "user/profile";
	}

	@RequestMapping("/delete/{cid}")
	@Transactional
	public String contactDelete(@PathVariable("cid") Integer cid, Model model, HttpSession session,
			Principal principal) {

		Contact contact = this.contactRepo.findById(cid).get();

		System.out.println("contact" + contact.getcId());

		// contact.setUser(null);

		// check....

		// this.contactRepo.delete(contact);

		User user = this.userRepo.findByUserName(principal.getName());

		user.getContact().remove(contact);

		this.userRepo.save(user);

		System.out.println("DELETE CONTACT");

		session.setAttribute("message", new Message("Contact delete successfully", "success"));

		return "redirect:/user/shows/0"; // Redirect to show updated list
	}

	// Open update form in controller
	@RequestMapping("/updatecontact/{cid}")
	public String update(@PathVariable("cid") Integer cid, Model model) {

		Contact contact = this.contactRepo.findById(cid).get();

		model.addAttribute("contact", contact);

		return "user/update";
	}

	// update data save handler in user controller
	@RequestMapping("/update")
	public String updateHandeller(@ModelAttribute Contact contact, @RequestParam("profileimage") MultipartFile file,
			Model m, HttpSession session, Principal principal) {
		try {
			// Optional: handle image updating here if needed

			User user = this.userRepo.findByUserName(principal.getName());
			contact.setUser(user);

			this.contactRepo.save(contact);

			// Set success message (optional)
			session.setAttribute("message", new Message("Contact updated successfully", "success"));

		} catch (Exception e) {
			session.setAttribute("message", new Message("Something went wrong!", "danger"));
			e.printStackTrace();
		}

		// ✅ FIX: redirect to pagination controller so currentPage is properly set
		return "redirect:/user/shows/0";
	}

	// opean setting handler
	@GetMapping("/settingpage")
	public String openSetting() {

		return "user/settings";
	}

	@PostMapping("/changepass")
	public String changePassword(HttpSession session, Model model, Principal principal,
			@RequestParam("oldpass") String oldpass, @RequestParam("newpass") String newpass) {

		String username = principal.getName();

		User user = this.userRepo.findByUserName(username);

		System.out.println("oldpass" + user.getPassword());

		if (this.passwordencoder.matches(oldpass, user.getPassword())) {

			user.setPassword(this.passwordencoder.encode(newpass));
			this.userRepo.save(user);
			session.setAttribute("message", new Message("Your password is successfully changed", "success"));
		} else {
			session.setAttribute("message", new Message("Please Enter your old password", "danger"));
			return "/user/settings";
		}

		System.out.println(oldpass);
		System.out.println(newpass);
		return "redirect:/user/index";
	}

	@PostMapping("/update-profile")
	public String updateUserProfile(@RequestParam("name") String name, @RequestParam("email") String email,

			Principal principal, HttpSession session) {

		try {
			String username = principal.getName();
			User user = this.userRepo.findByUserName(username);

			user.setName(name);
			user.setEmail(email);

			this.userRepo.save(user);

			session.setAttribute("message", new Message("Profile updated successfully", "success"));

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("Failed to update profile", "danger"));
		}

		return "redirect:/user/settingpage";
	}

	@GetMapping("/feedback")
	public String showFeedbackForm(Model model) {
		model.addAttribute("title", "Feedback");
		return "user/feedback_form";
	}

	@PostMapping("/user/submit-feedback")
	public String submitFeedback(@RequestParam("subject") String subject,
	                             @RequestParam("email") String email,
	                             @RequestParam("message") String message,
	                             HttpSession session) {

	    emailService.sendFeedbackEmail(subject, email, message);

	    session.setAttribute("message", new Message("Thank you for your feedback!", "alert-success"));
	    return "redirect:user/feedback_form"; // redirect to the feedback form page
	}

}
