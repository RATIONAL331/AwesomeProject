package com.rational.awesomeproject.service.auth;

import com.rational.awesomeproject.repository.AwesomeUserReactiveRepository;
import com.rational.awesomeproject.service.auth.dto.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomAuthenticationManager implements ReactiveAuthenticationManager {
	private final JwtService jwtService;
	private final AwesomeUserReactiveRepository userReactiveRepository;

	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		String authToken = authentication.getCredentials().toString();
		String uid = jwtService.getUidFromToken(authToken);
		return Mono.just(jwtService.validateToken(authToken))
		           .filter(valid -> valid)
		           .switchIfEmpty(Mono.empty())
		           .flatMap(valid -> userReactiveRepository.findById(uid))
		           .map(user -> new UsernamePasswordAuthenticationToken(new CustomUserPrincipal(user),
		                                                                user.getPassword(),
		                                                                user.getAuthorities()));
	}
}
