package com.rational.awesomeproject.controller.dto;

import com.rational.awesomeproject.common.enums.StorageExtType;
import com.rational.awesomeproject.service.dto.AwesomeStorageDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AwesomeStorageInfoResponse {
	private String id;
	private String parentStorageId;
	private String storageName;
	private long storageFileSize;
	private StorageExtType extType;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static AwesomeStorageInfoResponse of(AwesomeStorageDto storageDto) {
		return AwesomeStorageInfoResponse.builder()
		                                 .id(storageDto.getId())
		                                 .parentStorageId(storageDto.getParentStorageId())
		                                 .storageName(storageDto.getStorageName())
		                                 .storageFileSize(storageDto.getStorageFileSize())
		                                 .extType(storageDto.getExtType())
		                                 .createdAt(storageDto.getCreatedAt())
		                                 .updatedAt(storageDto.getUpdatedAt())
		                                 .build();
	}
}
