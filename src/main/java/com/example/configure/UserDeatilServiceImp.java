package com.example.configure;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.entites.User;
import com.example.repository.UserRepository;
@Service
public class UserDeatilServiceImp implements UserDetailsService {
	@Autowired
	private UserRepository springReps;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		// fetch data form user

		User user = springReps.findByUserName(username);

		if (user == null) {
			throw new UsernameNotFoundException("could not found exeption");

		}
		CustomUserDetail customUser = new CustomUserDetail(user);

		return customUser;
	}

}
