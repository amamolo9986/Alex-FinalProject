package com.hotnslicy.finalproject.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginLogoutController {

	@GetMapping("/login")
	public String getLogin() {
		return "login";
	}
	
	@GetMapping("/login-error")
	public String getLoginError(Model model) {
		model.addAttribute("loginError", true);
		return "login";
	}
}
