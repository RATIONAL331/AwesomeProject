package com.rational.awesomeproject.controller;

import com.rational.awesomeproject.controller.dto.AwesomeUserInfoResponse;
import com.rational.awesomeproject.controller.dto.CreateUserRequest;
import com.rational.awesomeproject.service.AwesomeStorageService;
import com.rational.awesomeproject.service.AwesomeUserService;
import com.rational.awesomeproject.service.dto.AwesomeStorageDto;
import com.rational.awesomeproject.service.dto.AwesomeUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class AwesomeUserRestController {
	private final AwesomeUserService userService;
	private final AwesomeStorageService storageService;

	@GetMapping("/user-info")
	public ResponseEntity<Mono<AwesomeUserInfoResponse>> getUserInfo() {
		String userId = "";
		Mono<AwesomeUserDto> awesomeUserMono = userService.getUserByUserId(userId);
		Mono<AwesomeStorageDto> awesomeStorageMono = storageService.getRootStorageByUserId(userId);

		return ResponseEntity.ok(Mono.zip(awesomeUserMono, awesomeStorageMono)
		                             .map(tuple -> AwesomeUserInfoResponse.of(tuple.getT1(), tuple.getT2())));
	}

	@PostMapping("/create-user")
	public ResponseEntity<Mono<AwesomeUserInfoResponse>> createUser(@RequestBody Mono<CreateUserRequest> request) {
		Mono<AwesomeUserDto> awesomeUserMono = request.flatMap(req -> userService.createUser(req.getUsername(), req.getPassword()));

		return ResponseEntity.ok(awesomeUserMono.zipWhen(user -> storageService.getRootStorageByUserId(user.getId()))
		                                        .map(tuple -> AwesomeUserInfoResponse.of(tuple.getT1(), tuple.getT2())));
	}
}
