package com.rational.awesomeproject.service.dto;

import com.rational.awesomeproject.repository.model.AwesomeUser;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AwesomeUserDto {
	private String id;
	private String username;
	private String rootStorageId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static AwesomeUserDto of(AwesomeUser awesomeUser) {
		return AwesomeUserDto.builder()
		                     .id(awesomeUser.getId())
		                     .username(awesomeUser.getUsername())
		                     .rootStorageId(awesomeUser.getRootStorageId())
		                     .createdAt(awesomeUser.getCreatedAt())
		                     .updatedAt(awesomeUser.getUpdatedAt())
		                     .build();
	}
}
