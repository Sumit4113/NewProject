package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.entites.User;
import com.example.repository.UserRepository;

import org.springframework.ui.Model;

@Controller
public class MyController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@RequestMapping("/")
	public String base(Model model , User user) {
		model.addAttribute("user2", user);
		return "home";
	}
	

	

	@RequestMapping("/about")
	public String view2(Model model , User user) {
		model.addAttribute("user2", user);
		return "base";
	}
	
	

	// handler for registering user
	@RequestMapping("/registers")
	public String registerUser(@ModelAttribute("user2") User user, Model model, RedirectAttributes redirectAttribute) {

		try {
			User email = this.userRepository.findByUserName(user.getEmail());

			if (email != null) {
				redirectAttribute.addFlashAttribute("error", "Email already registered!");
				return "redirect:/#register";
			}

			// Encrypt password before saving
			user.setRole("ROLE_USER");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			userRepository.save(user);

			redirectAttribute.addFlashAttribute("success", "Registration successful! Please login.");
			return "redirect:/#register";

		} catch (Exception e) {
			redirectAttribute.addFlashAttribute("error", "An error occurred: " + e.getMessage());
			return "redirect:/#register";
		}
	}

	// custom login page
	@RequestMapping("/login")
	public String loginPage(Model model) {

		User user = new User();

		model.addAttribute("login", user);

		return "login";
	}

}
