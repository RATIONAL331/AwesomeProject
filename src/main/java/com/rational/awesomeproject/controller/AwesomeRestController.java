package com.rational.awesomeproject.controller;

import com.rational.awesomeproject.service.AwesomeStorageService;
import com.rational.awesomeproject.service.AwesomeUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AwesomeRestController {
	private final AwesomeUserService userService;
	private final AwesomeStorageService storageService;

	@GetMapping("/")
	public String test() {
		return "hello";
	}
}
