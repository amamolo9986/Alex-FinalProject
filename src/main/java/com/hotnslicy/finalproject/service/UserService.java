package com.hotnslicy.finalproject.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hotnslicy.finalproject.domain.User;
import com.hotnslicy.finalproject.repository.UserRepository;

@Service
public class UserService implements UserDetailsService{
	
	private UserRepository userRepo;

	public UserService(UserRepository userRepo) {
		super();
		this.userRepo = userRepo;
	} 

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.findByUsername(username);
		if (user == null) throw new UsernameNotFoundException("Bad Credentials");
		return user;
	}
	
	public Optional<User> findById(Integer userId) {
		return userRepo.findById(userId);
	}

}
