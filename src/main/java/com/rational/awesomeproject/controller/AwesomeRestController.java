package com.rational.awesomeproject.controller;

import com.rational.awesomeproject.controller.dto.CreateUserRequest;
import com.rational.awesomeproject.repository.model.AwesomeUser;
import com.rational.awesomeproject.service.AwesomeStorageService;
import com.rational.awesomeproject.service.AwesomeUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class AwesomeRestController {
	private final AwesomeUserService userService;
	private final AwesomeStorageService storageService;

	@GetMapping("/")
	public String test() {
		return "hello";
	}

	@PostMapping("/create-user")
	public ResponseEntity<Mono<AwesomeUser>> createUser(@RequestBody Mono<CreateUserRequest> request) {
		Mono<AwesomeUser> awesomeUserMono = request.flatMap(req -> userService.createUser(req.getUsername(), req.getPassword()));
		return ResponseEntity.ok(awesomeUserMono);
	}
}
