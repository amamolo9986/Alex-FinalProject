package com.hotnslicy.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotnslicy.finalproject.domain.User;

public interface UserRepository extends JpaRepository<User, Integer>{
	
	User findByUsername(String username);

}
