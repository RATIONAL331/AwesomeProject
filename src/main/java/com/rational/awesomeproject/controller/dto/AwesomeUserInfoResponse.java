package com.rational.awesomeproject.controller.dto;

import com.rational.awesomeproject.service.dto.AwesomeStorageDto;
import com.rational.awesomeproject.service.dto.AwesomeUserDto;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AwesomeUserInfoResponse {
	private String id;
	private String username;
	private String rootStorageId;
	private long storageFileSize;
	private OffsetDateTime createdAt;
	private OffsetDateTime updatedAt;

	public static AwesomeUserInfoResponse of(AwesomeUserDto userDto,
	                                         AwesomeStorageDto storageDto) {
		return AwesomeUserInfoResponse.builder()
		                              .id(userDto.getId())
		                              .username(userDto.getUsername())
		                              .rootStorageId(userDto.getRootStorageId())
		                              .storageFileSize(storageDto.getStorageFileSize())
		                              .createdAt(userDto.getCreatedAt())
		                              .updatedAt(userDto.getUpdatedAt())
		                              .build();
	}
}
