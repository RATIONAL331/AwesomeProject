package com.rational.awesomeproject.config;

import com.rational.awesomeproject.service.auth.CustomAuthenticationManager;
import com.rational.awesomeproject.service.auth.CustomSecurityContextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final CustomAuthenticationManager authenticationManager;
	private final CustomSecurityContextRepository securityContextRepository;

	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		http.authorizeExchange(exchanges -> exchanges.pathMatchers("/auth/register", "/auth/login").permitAll()
		                                             .anyExchange().authenticated())
		    .formLogin().disable()
		    .csrf().disable()
		    .cors().disable()
		    .exceptionHandling()
		    .authenticationEntryPoint((exchange, e) -> Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
		    .accessDeniedHandler((exchange, e) -> Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
		    .and()
		    .securityContextRepository(securityContextRepository)
		    .authenticationManager(authenticationManager);
		return http.build();
	}
}
