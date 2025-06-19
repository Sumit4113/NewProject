package com.example.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.example.entites.User;
import com.example.repository.UserRepository;
import com.example.services.CloudinaryService;

import org.springframework.ui.Model;

@Controller
public class MyController {
	@Autowired
	private CloudinaryService cloudinaryService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@RequestMapping("/")
	public String base(Model model, User user) {
		model.addAttribute("user2", user);
		return "home";
	}

	@GetMapping("/register")
	public String registerPage(Model model) {
		model.addAttribute("title", "Register Page");
		model.addAttribute("User2", new User());
		return "register";
	}

	@PostMapping("/registers")
	public String registerUser(@ModelAttribute("User2") User user, @RequestParam("profileImage") MultipartFile file,
			RedirectAttributes redirectAttribute) {

		try {
			// Check for existing user
			User existing = this.userRepository.findByUserName(user.getEmail());
			if (existing != null) {
				redirectAttribute.addFlashAttribute("error", "Email already registered!");
				return "redirect:/register";
			}

			// Set role and encrypt password
			user.setRole("ROLE_USER");
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			// ✅ Upload image to Firebase and save URL
			if (!file.isEmpty()) {
				String imageUrl = cloudinaryService.uploadFile(file);
				user.setImageUrl(imageUrl);// ✅ Save image URL, not bytes
			}

			// Save user
			userRepository.save(user);

			redirectAttribute.addFlashAttribute("success", "Registration successful! Please login.");
			return "redirect:/register";

		} catch (Exception e) {
			redirectAttribute.addFlashAttribute("error", "An error occurred: " + e.getMessage());
			return "redirect:/register";
		}
	}

	// custom login page
	@RequestMapping("/loginPage")
	public String loginPage(Model model, RedirectAttributes redirectAttributes) {

		User user = new User();

		model.addAttribute("user", user);
		redirectAttributes.addFlashAttribute("error", "Invalid credentials");
		return "login";
	}

//	@GetMapping("/user/image/{id}")
//	@ResponseBody
//	public ResponseEntity<byte[]> getUserImage(@PathVariable("id") int id) {
//		Optional<User> userOpt = userRepository.findById(id);
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

}
