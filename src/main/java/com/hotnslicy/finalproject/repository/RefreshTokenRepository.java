package com.hotnslicy.finalproject.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotnslicy.finalproject.domain.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer>{

	Optional<RefreshToken> findByRefreshToken(String refreshToken); 

}
