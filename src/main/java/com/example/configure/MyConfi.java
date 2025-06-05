package com.example.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class MyConfi {

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

	@Bean
	public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain securityFilter(HttpSecurity http) throws Exception {
		http.authenticationProvider(daoAuthentication())

				.authorizeHttpRequests(requests -> requests.requestMatchers("/admin/**").hasRole("ADMIN")
						.requestMatchers("/user/**").hasRole("USER").requestMatchers("/api/auth/**").permitAll() // allow
																													// API
																													// login
						.requestMatchers("/**").permitAll().anyRequest().authenticated())

				// 🔐 Form Login setup
				.formLogin(form -> form.loginPage("/loginPage").loginProcessingUrl("/dologin")
						.defaultSuccessUrl("/user/index", true).permitAll())

				// 🔐 Logout setup
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/"))

				// 🔐 Disable CSRF for API (you may need finer control in production)
				.csrf(csrf -> csrf.disable());

		return http.build();
	}
}
