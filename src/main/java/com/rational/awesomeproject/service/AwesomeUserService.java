package com.rational.awesomeproject.service;

import com.rational.awesomeproject.repository.model.AwesomeUser;
import reactor.core.publisher.Mono;

public interface AwesomeUserService {
	Mono<AwesomeUser> createUser(String username, String rawPassword);
}
