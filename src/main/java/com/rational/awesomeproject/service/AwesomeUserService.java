package com.rational.awesomeproject.service;

import com.rational.awesomeproject.service.dto.AwesomeUserDto;
import reactor.core.publisher.Mono;

public interface AwesomeUserService {
	Mono<AwesomeUserDto> createUser(String username, String rawPassword);

	Mono<AwesomeUserDto> getUserByUserId(String userId);

	Mono<AwesomeUserDto> getUserByUsername(String username);
}
