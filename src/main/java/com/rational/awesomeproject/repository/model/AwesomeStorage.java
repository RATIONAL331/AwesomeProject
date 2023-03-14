package com.rational.awesomeproject.repository.model;

import com.rational.awesomeproject.common.enums.StorageExtType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

@Getter
@Setter
@Document
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AwesomeStorage {
	@Id
	private String id;

	private String userId;
	private String parentStorageId;
	private String storageName;
	private long storageFileSize;
	private StorageExtType extType;

	@CreatedDate
	private OffsetDateTime createdAt;

	@LastModifiedDate
	private OffsetDateTime updatedAt;
	private OffsetDateTime deletedAt;

	public static AwesomeStorage makeDefault(String userId) {
		AwesomeStorage awesomeStorage = new AwesomeStorage();
		awesomeStorage.setUserId(userId);
		awesomeStorage.setParentStorageId(null);
		awesomeStorage.setStorageName("root::" + userId);
		awesomeStorage.setStorageFileSize(0);
		awesomeStorage.setExtType(StorageExtType.FOLDER);

		OffsetDateTime now = OffsetDateTime.now();
		awesomeStorage.setCreatedAt(now);
		awesomeStorage.setUpdatedAt(now);
		awesomeStorage.setCreatedAt(null);
		return awesomeStorage;
	}
}
