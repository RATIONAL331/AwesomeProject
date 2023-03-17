package com.rational.awesomeproject.service;

import com.rational.awesomeproject.service.dto.AwesomeStorageDto;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AwesomeStorageService {
	Mono<AwesomeStorageDto> getStorageById(String userId, String storageId);

	Flux<AwesomeStorageDto> getStorageByParentStorageId(String userId, String parentStorageId);

	Mono<AwesomeStorageDto> getRootStorageByUserId(String userId);

	Mono<AwesomeStorageDto> makeFolder(String userId, String parentStorageId, String folderName);

	Mono<AwesomeStorageDto> saveStorage(String userId, String parentStorageId, FilePart filePart);

	Mono<Boolean> removeStorageByStorageId(String userId, String storageId);
}
