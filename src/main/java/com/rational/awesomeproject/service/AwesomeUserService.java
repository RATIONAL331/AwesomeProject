package com.rational.awesomeproject.service;

import com.rational.awesomeproject.service.dto.AwesomeUserDto;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Mono;

public interface AwesomeUserService extends ReactiveUserDetailsService {
	Mono<AwesomeUserDto> createUser(String username, String rawPassword);

	Mono<AwesomeUserDto> getUserByUserId(String userId);

	Mono<AwesomeUserDto> getUserByUsername(String username);
}
