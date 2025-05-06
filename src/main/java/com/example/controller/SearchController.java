package com.example.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.entites.Contact;
import com.example.entites.User;
import com.example.repository.ContactRepository;
import com.example.repository.UserRepository;


//Here is my Search API
@RestController
public class SearchController {
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private ContactRepository contactrepo;

	@GetMapping("search/(query)")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal) {

		System.out.println(query);
		User user = this.userRepo.findByUserName(principal.getName());

		List<Contact> contacts = this.contactrepo.findByUserNameContainingAndUser(query, user);

		return ResponseEntity.ok(contacts);
	}
}
