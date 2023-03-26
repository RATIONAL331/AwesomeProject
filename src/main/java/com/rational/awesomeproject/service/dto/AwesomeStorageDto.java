package com.rational.awesomeproject.service.dto;

import com.rational.awesomeproject.common.enums.StorageExtType;
import com.rational.awesomeproject.repository.model.AwesomeStorage;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AwesomeStorageDto {
	private String id;
	private String userId;
	private String parentStorageId;
	private String storageName;
	private long storageFileSize;
	private StorageExtType extType;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime deletedAt;

	public static AwesomeStorageDto of(AwesomeStorage awesomeStorage) {
		return AwesomeStorageDto.builder()
		                        .id(awesomeStorage.getId())
		                        .userId(awesomeStorage.getUserId())
		                        .parentStorageId(awesomeStorage.getParentStorageId())
		                        .storageName(awesomeStorage.getStorageName())
		                        .storageFileSize(awesomeStorage.getStorageFileSize())
		                        .extType(awesomeStorage.getExtType())
		                        .createdAt(awesomeStorage.getCreatedAt())
		                        .updatedAt(awesomeStorage.getUpdatedAt())
		                        .deletedAt(awesomeStorage.getDeletedAt())
		                        .build();
	}
}
