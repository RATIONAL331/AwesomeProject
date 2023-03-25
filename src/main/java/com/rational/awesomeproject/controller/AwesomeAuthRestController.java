package com.rational.awesomeproject.controller;

import com.rational.awesomeproject.controller.dto.AwesomeUserInfoResponse;
import com.rational.awesomeproject.controller.dto.CreateUserRequest;
import com.rational.awesomeproject.controller.dto.LoginRequest;
import com.rational.awesomeproject.controller.dto.LoginResponse;
import com.rational.awesomeproject.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AwesomeAuthRestController {
	private final AuthService authService;

	@PostMapping("/register")
	public Mono<AwesomeUserInfoResponse> createUser(@RequestBody Mono<CreateUserRequest> request) {
		return request.flatMap(authService::register);
	}

	@PostMapping("/login")
	public Mono<LoginResponse> login(@RequestBody Mono<LoginRequest> loginRequest) {
		return loginRequest.flatMap(authService::login);
	}
}
