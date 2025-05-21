package com.example.configure;

import org.springframework.context.annotation.Bean;





import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
public class MyConfi    {

	@Bean
	public UserDetailsService getUserDetailService() {
		return new UserDeatilServiceImp();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	

	@Bean
	public DaoAuthenticationProvider daoAuthentication() {

		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

		daoAuthenticationProvider.setUserDetailsService(this.getUserDetailService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;
	}

	// configure method

	@Bean
	public SecurityFilterChain securityFilter(HttpSecurity http) throws Exception {
		http.authenticationProvider(daoAuthentication());
		http.authorizeHttpRequests(requests -> requests
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.requestMatchers("/user/**").hasRole("ROLE_USER")
				.requestMatchers("/**").permitAll()
				.anyRequest().authenticated()
				)
		        
		            .formLogin(form -> form
	                .loginPage("/")
	                .loginProcessingUrl("/dologin")
	                .defaultSuccessUrl("/user/index", true)
	                .permitAll()
	             
	            )
		            
		           .logout(logout ->logout
		        		   .logoutUrl("/logout")
		        		   .logoutSuccessUrl("/")
		        		      
		        		   
		        		   ) 
		            
				.csrf(csrf -> csrf.disable());

		return http.build();
	}
}
