package com.rational.awesomeproject.controller;

import com.rational.awesomeproject.controller.dto.AwesomeUserInfoResponse;
import com.rational.awesomeproject.controller.dto.CreateUserRequest;
import com.rational.awesomeproject.controller.dto.LoginRequest;
import com.rational.awesomeproject.controller.dto.LoginResponse;
import com.rational.awesomeproject.service.auth.AuthService;
import com.rational.awesomeproject.service.auth.dto.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
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

	@GetMapping("/test")
	public Mono<String> test(@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal) {
		System.out.println(customUserPrincipal.getUser().getUsername());
		System.out.println(customUserPrincipal.getUser().getId());
		return Mono.just("test");
	}
}
