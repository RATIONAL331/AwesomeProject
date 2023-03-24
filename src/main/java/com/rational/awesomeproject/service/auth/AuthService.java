package com.rational.awesomeproject.service.auth;

import com.rational.awesomeproject.common.CustomException;
import com.rational.awesomeproject.controller.dto.AwesomeUserInfoResponse;
import com.rational.awesomeproject.controller.dto.CreateUserRequest;
import com.rational.awesomeproject.controller.dto.LoginRequest;
import com.rational.awesomeproject.controller.dto.LoginResponse;
import com.rational.awesomeproject.repository.AwesomeUserReactiveRepository;
import com.rational.awesomeproject.service.AwesomeStorageService;
import com.rational.awesomeproject.service.AwesomeUserService;
import com.rational.awesomeproject.service.dto.AwesomeUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final PasswordEncoder passwordEncoder;
	private final AwesomeUserReactiveRepository userReactiveRepository;
	private final JwtService jwtService;
	private final AwesomeUserService userService;
	private final AwesomeStorageService storageService;

	public Mono<AwesomeUserInfoResponse> register(CreateUserRequest request) {
		Mono<AwesomeUserDto> awesomeUserMono = userService.createUser(request.getUsername(), request.getPassword());
		return awesomeUserMono.zipWhen(user -> storageService.getRootStorageByUserId(user.getId()))
		                      .map(tuple -> AwesomeUserInfoResponse.of(tuple.getT1(), tuple.getT2()));
	}

	public Mono<LoginResponse> login(LoginRequest loginRequest) {
		return userReactiveRepository.findByUsername(loginRequest.getUsername())
		                             .switchIfEmpty(Mono.error(CustomException.of(HttpStatus.UNAUTHORIZED)))
		                             .flatMap(user -> {
			                             if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
				                             return Mono.just(user);
			                             } else {
				                             return Mono.error(CustomException.of(HttpStatus.UNAUTHORIZED));
			                             }
		                             })
		                             .flatMap(user -> {
			                             Mono<String> accessTokenMono = jwtService.generateAccessToken(user.getId());
			                             Mono<String> refreshTokenMono = jwtService.generateRefreshToken(user.getId());
			                             return Mono.zip(accessTokenMono, refreshTokenMono)
			                                        .flatMap(tokens -> Mono.just(LoginResponse.success(tokens.getT1(), tokens.getT2())));
		                             });
	}
}
