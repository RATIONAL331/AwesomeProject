package com.rational.awesomeproject.common;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomException extends RuntimeException {
	private HttpStatus httpStatus;
	private String message;

	public static CustomException of(HttpStatus httpStatus) {
		return CustomException.builder()
		                      .httpStatus(httpStatus)
		                      .message(httpStatus.getReasonPhrase())
		                      .build();

	}
}
