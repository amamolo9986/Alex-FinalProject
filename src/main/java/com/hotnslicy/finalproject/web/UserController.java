package com.hotnslicy.finalproject.web;

import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotnslicy.finalproject.domain.RefreshToken;
import com.hotnslicy.finalproject.domain.User;
import com.hotnslicy.finalproject.repository.UserRepository;
import com.hotnslicy.finalproject.request.RefreshTokenRequest;
import com.hotnslicy.finalproject.response.AuthenticationResponse;
import com.hotnslicy.finalproject.response.RefreshTokenResponse;
import com.hotnslicy.finalproject.service.JwtService;
import com.hotnslicy.finalproject.service.RefreshTokenService;
import com.hotnslicy.finalproject.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	private UserRepository userRepo;
	private PasswordEncoder passwordEncoder;
	private JwtService jwtService;
	private UserService userService;
	private RefreshTokenService refreshTokenService;

	public UserController(UserRepository userRepo, PasswordEncoder passwordEncoder, JwtService jwtService,
			UserService userService, RefreshTokenService refreshTokenService) {
		super();
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.userService = userService;
		this.refreshTokenService = refreshTokenService;
	}

	@PostMapping("")
	public ResponseEntity<AuthenticationResponse> signUpUser(@RequestBody User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		User savedUser = userRepo.save(user);
		
		String accessToken = jwtService.generateToken(new HashMap<>(), savedUser);
		RefreshToken refreshToken = refreshTokenService.generateRefreshToken(savedUser.getId());
		
		return ResponseEntity.ok(new AuthenticationResponse(savedUser.getUsername(), accessToken, refreshToken.getRefreshToken()));
	}
	
	@PostMapping("/login")
	public ResponseEntity<AuthenticationResponse> signInUser(@RequestBody User user){
		User loggedInUser = (User) userService.loadUserByUsername(user.getUsername());
		
		String accessToken = jwtService.generateToken(new HashMap<>(), loggedInUser);
		RefreshToken refreshToken = refreshTokenService.generateRefreshToken(loggedInUser.getId());
		
		return ResponseEntity.ok(new AuthenticationResponse(loggedInUser.getUsername(), accessToken, refreshToken.getRefreshToken()));
	}
	
	@PostMapping("/refreshtoken")
	public ResponseEntity<RefreshTokenResponse> getNewAccessToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
		String accessToken = refreshTokenService.createNewAccessToken(refreshTokenRequest);
		
		return ResponseEntity.ok(new RefreshTokenResponse(accessToken, refreshTokenRequest.refreshToken()));
	}

}
