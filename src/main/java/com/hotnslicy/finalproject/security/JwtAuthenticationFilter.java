package com.hotnslicy.finalproject.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hotnslicy.finalproject.request.RefreshTokenRequest;
import com.hotnslicy.finalproject.service.JwtService;
import com.hotnslicy.finalproject.service.RefreshTokenService;
import com.hotnslicy.finalproject.service.UserService;
import com.hotnslicy.finalproject.utils.CookieUtils;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private JwtService jwtService;
	private UserService userService;
	private RefreshTokenService refreshTokenService;

	public JwtAuthenticationFilter(JwtService jwtService, UserService userService,
			RefreshTokenService refreshTokenService) {
		super();
		this.jwtService = jwtService;
		this.userService = userService;
		this.refreshTokenService = refreshTokenService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		Cookie accessTokenCookie = null;
		Cookie refreshTokenCookie = null;

		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals(CookieUtils.ACCESS_TOKEN_NAME)) {
					accessTokenCookie = cookie;
				} else if (cookie.getName().equals(CookieUtils.REFRESH_TOKEN_NAME)) {
					refreshTokenCookie = cookie;
				}
			}
		}

		if (accessTokenCookie != null) {

			int loginTryCount = 0;
			String token = accessTokenCookie.getValue();
			while (loginTryCount <= 2) {
				try {
					String subject = jwtService.getSubject(token);
					Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); 

					if (StringUtils.hasText(subject) && authentication == null) {
						UserDetails userDetails = userService.loadUserByUsername(subject);

						if (jwtService.isValidToken(token, userDetails)) {
							SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
							UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
									userDetails, userDetails.getPassword(), userDetails.getAuthorities());
							securityContext.setAuthentication(authToken);
							SecurityContextHolder.setContext(securityContext);
							break;
						}
					}
				} catch (ExpiredJwtException e) {
					try {
						token = refreshTokenService.createNewAccessToken(new RefreshTokenRequest(refreshTokenCookie.getValue()));
						accessTokenCookie = CookieUtils.createAccesTokenCookie(token);
						
						response.addCookie(accessTokenCookie);
					} catch (Exception e1) {
						//there was a problem creating a new access token,
						//we're ignoring this error on purpose in order to allow
						//the slow of the filter chain to continue
					}
				}
				loginTryCount++;
			}

		}

		filterChain.doFilter(request, response);
	}

}

