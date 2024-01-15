package com.hotnslicy.finalproject.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hotnslicy.finalproject.domain.RefreshToken;
import com.hotnslicy.finalproject.domain.User;
import com.hotnslicy.finalproject.repository.RefreshTokenRepository;
import com.hotnslicy.finalproject.request.RefreshTokenRequest;

@Service
public class RefreshTokenService {
	
	@Value("${jwt.refreshTokenExpirationTimeInMillis}")
	private Long refreshTokenExpirationTimeInMillis;

	private UserService userService;
	private RefreshTokenRepository refreshTokenRepo;
	private JwtService jwtService;

	public RefreshTokenService(UserService userService, RefreshTokenRepository refreshTokenRepo,
			JwtService jwtService) {
		super();
		this.userService = userService;
		this.refreshTokenRepo = refreshTokenRepo;
		this.jwtService = jwtService;
	}

	public RefreshToken generateRefreshToken(Integer userId) {
		Optional<User> userOpt = userService.findById(userId);
		if (userOpt.isPresent()) {
			Optional<RefreshToken> refreshTokenOpt = refreshTokenRepo.findById(userId);
			RefreshToken refreshToken = null;
			if(refreshTokenOpt.isPresent()) {
				refreshToken = refreshTokenOpt.get();
				refreshToken.setExpirationDate(genetateRefreshTokenExpirationDate());
				refreshToken.setRefreshToken(generateRandomTokenValue());
			} else {
				refreshToken = new RefreshToken(userOpt.get(), generateRandomTokenValue(), genetateRefreshTokenExpirationDate());
			}
			refreshToken = refreshTokenRepo.save(refreshToken);
			return refreshToken;

		}
		return null;
	}

	public String generateRandomTokenValue() {
		return UUID.randomUUID().toString();
	}

	public Date genetateRefreshTokenExpirationDate() {
		return new Date(System.currentTimeMillis() + refreshTokenExpirationTimeInMillis);
	}

	public String createNewAccessToken(RefreshTokenRequest refreshTokenRequest) {
		Optional<RefreshToken> refreshTokenOpt = refreshTokenRepo.findByRefreshToken(refreshTokenRequest.refreshToken());
		
		
		String accessToken = refreshTokenOpt.map(RefreshTokenService::isNotExpired)
				.map(refreshToken -> jwtService.generateToken(new HashMap<>(), refreshToken.getUser()))
				.orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));
		return accessToken; 
	}
	
	private static RefreshToken isNotExpired(RefreshToken refreshToken) {
		if(refreshToken.getExpirationDate().after(new Date())) {
			return refreshToken;
		} else {
			throw new IllegalArgumentException("Refresh token has expired");
		}
	}
	
	
}
