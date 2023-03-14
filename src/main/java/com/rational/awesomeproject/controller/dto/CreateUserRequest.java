package com.rational.awesomeproject.controller.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateUserRequest {
	private String username;
	private String password;
}
