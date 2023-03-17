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
		return makeFolder(userId, null, "root::" + userId);
	}

	public static AwesomeStorage makeFile(String userId, String parentStorageId, String fileName, long fileSize) {
		return makeStorage(userId, parentStorageId, fileName, fileSize, StorageExtType.FILE);
	}

	public static AwesomeStorage makeFolder(String userId, String parentStorageId, String folderName) {
		return makeStorage(userId, parentStorageId, folderName, 0, StorageExtType.FOLDER);
	}

	public static AwesomeStorage makeStorage(String userId, String parentStorageId, String fileName, long fileSize, StorageExtType extType) {
		AwesomeStorage awesomeStorage = new AwesomeStorage();
		awesomeStorage.setUserId(userId);
		awesomeStorage.setParentStorageId(parentStorageId);
		awesomeStorage.setStorageName(fileName);
		awesomeStorage.setStorageFileSize(fileSize);
		awesomeStorage.setExtType(extType);

		OffsetDateTime now = OffsetDateTime.now();
		awesomeStorage.setCreatedAt(now);
		awesomeStorage.setUpdatedAt(now);
		return awesomeStorage;
	}
}
