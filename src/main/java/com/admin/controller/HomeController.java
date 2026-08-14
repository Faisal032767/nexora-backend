package com.admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
	@GetMapping("/test")
	public String test() {
		return "testing";
	}

	@GetMapping("/protected")
	public String protectedEndpoint() {
		return "You are authenticated!";
	}
}
