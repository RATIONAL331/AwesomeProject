package com.rational.awesomeproject.controller.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginResponse {
	private boolean success;
	private String accessToken;
	private String refreshToken;

	public static LoginResponse success(String accessToken, String refreshToken) {
		return LoginResponse.builder()
		                    .success(true)
		                    .accessToken(accessToken)
		                    .refreshToken(refreshToken)
		                    .build();
	}

	public static LoginResponse fail() {
		return LoginResponse.builder()
		                    .success(false)
		                    .build();
	}
}
