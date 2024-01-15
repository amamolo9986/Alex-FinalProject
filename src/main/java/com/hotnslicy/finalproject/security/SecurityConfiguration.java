package com.hotnslicy.finalproject.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.hotnslicy.finalproject.repository.UserRepository;
import com.hotnslicy.finalproject.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	
	private UserRepository userRepo;
	private JwtAuthenticationFilter jwtAuthenticationFilter; 
	private LoginSuccessHandler loginSuccessHandler;

	public SecurityConfiguration(UserRepository userRepo, JwtAuthenticationFilter jwtAuthenticationFilter,
			LoginSuccessHandler loginSuccessHandler) {
		super();
		this.userRepo = userRepo;
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.loginSuccessHandler = loginSuccessHandler;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	 
	@Bean
	public UserDetailsService userDetailsService() {
		return new UserService(userRepo);
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
		    .authorizeHttpRequests((request) -> {
		    request
		    	.requestMatchers("/api/v1/users", "/api/v1/users/**").permitAll()
		    	.anyRequest().authenticated();
		})
		.sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.authenticationProvider(authenticationProvider())
		.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
		.formLogin(login -> {
			login.loginPage("/login");
			login.failureUrl("/login-error");
			login.successHandler(loginSuccessHandler);
			login.permitAll();
		});
		
 		return http.build();
 	}
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		daoAuthenticationProvider.setUserDetailsService(userDetailsService());
		return daoAuthenticationProvider;
	}

} 
