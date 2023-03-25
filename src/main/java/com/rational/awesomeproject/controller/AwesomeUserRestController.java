package com.rational.awesomeproject.controller;

import com.rational.awesomeproject.controller.dto.AwesomeUserInfoResponse;
import com.rational.awesomeproject.service.AwesomeStorageService;
import com.rational.awesomeproject.service.AwesomeUserService;
import com.rational.awesomeproject.service.auth.dto.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class AwesomeUserRestController {
	private final AwesomeUserService userService;
	private final AwesomeStorageService storageService;

	@GetMapping("/user-info")
	public Mono<AwesomeUserInfoResponse> getUserInfo(@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal) {
		String userId = customUserPrincipal.getUser().getId();
		return Mono.zip(userService.getUserByUserId(userId), storageService.getRootStorageByUserId(userId))
		           .map(tuple -> AwesomeUserInfoResponse.of(tuple.getT1(), tuple.getT2()));
	}
}
