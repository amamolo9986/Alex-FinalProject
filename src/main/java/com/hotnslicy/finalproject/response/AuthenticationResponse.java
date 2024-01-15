package com.hotnslicy.finalproject.response;

public record AuthenticationResponse(
		String username,
		String accessToken,
		String refreshToken) {

}
