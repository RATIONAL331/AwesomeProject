package com.rational.awesomeproject.controller;

import com.rational.awesomeproject.controller.dto.AwesomeUserInfoResponse;
import com.rational.awesomeproject.repository.model.AwesomeUser;
import com.rational.awesomeproject.service.AwesomeStorageService;
import com.rational.awesomeproject.service.AwesomeUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
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
	public Mono<AwesomeUserInfoResponse> getUserInfo() {
		return ReactiveSecurityContextHolder.getContext()
		                                    .map(securityContext -> securityContext.getAuthentication().getPrincipal())
		                                    .cast(AwesomeUser.class)
		                                    .map(AwesomeUser::getId)
		                                    .flatMap(id -> Mono.zip(userService.getUserByUserId(id), storageService.getRootStorageByUserId(id)))
		                                    .map(tuple -> AwesomeUserInfoResponse.of(tuple.getT1(), tuple.getT2()));
	}
}
