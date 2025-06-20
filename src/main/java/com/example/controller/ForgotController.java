package com.example.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.entites.User;
import com.example.repository.UserRepository;
import com.example.services.EmailApiService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {
	Random random = new Random();
	@Autowired
	private EmailApiService emailservice;

	@Autowired
	private UserRepository userrepo;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@RequestMapping("/forgot")
	public String openForgot() {

		return "forgotpage";
	}

	@PostMapping("/sendotps")
	public String sendOtp(@RequestParam("email") String email, HttpSession session) {
		int otp = random.nextInt(999999);
		String subject = "OTP FROM SCM";
		String message = "<h1> OTP =" + otp + "</h1>";

		boolean result = this.emailservice.sendEmail(subject, message, email);

		if (result) {
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email); // ✅ Store email in session
			return "verifyotp";
		} else {
			session.setAttribute("message", "Check your email id ");
			return "forgotpage";
		}
	}

	@RequestMapping("/verifyotp")
	public String verify(@RequestParam("otp") int otp, HttpSession session) {
		int myotp = (int) session.getAttribute("myotp");
		String email = (String) session.getAttribute("email");

		if (myotp == otp) {

			// password change
			return "passwordchange";
		} else {
			session.setAttribute("message", "Wrong Otp ");
			return "verifyotp";
		}

	}

	@RequestMapping("/passchange")
	public String change(@RequestParam("newpassword") String newpassword, HttpSession session,
			RedirectAttributes redirectAttributes) {
		String email = (String) session.getAttribute("email");

		if (email == null) {
			session.setAttribute("message", "Session expired. Please restart the forgot password process.");
			return "forgotpage"; // Redirect to start again
		}

		User user = this.userrepo.findByUserName(email);
		if (user == null) {
			session.setAttribute("message", "User not found.");
			return "forgotpage";
		}

		user.setPassword(this.passwordEncoder.encode(newpassword));
		this.userrepo.save(user);

		redirectAttributes.addFlashAttribute("message", "Password changed successfully. You can now login.");
		return "redirect:/loginPage";

	}

}
