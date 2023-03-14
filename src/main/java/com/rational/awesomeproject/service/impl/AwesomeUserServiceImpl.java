package com.rational.awesomeproject.service.impl;

import com.rational.awesomeproject.repository.AwesomeStorageReactiveRepository;
import com.rational.awesomeproject.repository.AwesomeUserReactiveRepository;
import com.rational.awesomeproject.repository.model.AwesomeStorage;
import com.rational.awesomeproject.repository.model.AwesomeUser;
import com.rational.awesomeproject.service.AwesomeUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AwesomeUserServiceImpl implements AwesomeUserService {
	private final PasswordEncoder passwordEncoder;
	private final AwesomeUserReactiveRepository userReactiveRepository;
	private final AwesomeStorageReactiveRepository storageReactiveRepository;

	@Override
	@Transactional
	public Mono<AwesomeUser> createUser(String username, String rawPassword) {
		return userReactiveRepository.findByUsername(username)
		                             .switchIfEmpty(initializeUser(username, rawPassword));
	}

	private Mono<AwesomeUser> initializeUser(String username, String rawPassword) {
		// make user temporary
		Mono<AwesomeUser> tempUser = userReactiveRepository.save(AwesomeUser.makeDefault(username, passwordEncoder.encode(rawPassword)));

		// make root storage -> link user -> update user
		return userReactiveRepository.save(AwesomeUser.makeDefault(username, passwordEncoder.encode(rawPassword)))
		                             .flatMap(user -> storageReactiveRepository.save(AwesomeStorage.makeDefault(user.getId())))
		                             .flatMap(storage -> tempUser.doOnNext(u -> u.setRootStorageId(storage.getId()))
		                                                         .flatMap(userReactiveRepository::save));
	}
}
