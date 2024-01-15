package com.hotnslicy.finalproject.security;

import java.io.IOException;
import java.util.HashMap;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.hotnslicy.finalproject.domain.RefreshToken;
import com.hotnslicy.finalproject.domain.User;
import com.hotnslicy.finalproject.service.JwtService;
import com.hotnslicy.finalproject.service.RefreshTokenService;
import com.hotnslicy.finalproject.utils.CookieUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

	private JwtService jwtService;
	private RefreshTokenService refreshTokenService;

	public LoginSuccessHandler(JwtService jwtService, RefreshTokenService refreshTokenService) {
		super();
		this.jwtService = jwtService;
		this.refreshTokenService = refreshTokenService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		User user = (User) authentication.getPrincipal();

		String accessToken = jwtService.generateToken(new HashMap<>(), user);
		RefreshToken refreshToken = refreshTokenService.generateRefreshToken(user.getId());

		Cookie accessTokenCookie = CookieUtils.createAccesTokenCookie(accessToken);
		Cookie refreshTokenCookie = CookieUtils.createRefreshTokenCookie(refreshToken.getRefreshToken());

		response.addCookie(refreshTokenCookie);
		response.addCookie(accessTokenCookie);
		response.sendRedirect("/products");

	}

}
