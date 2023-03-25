package com.rational.awesomeproject.service;

import com.rational.awesomeproject.repository.model.AwesomeStorage;
import com.rational.awesomeproject.service.dto.AwesomeStorageDto;
import org.springframework.core.io.Resource;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

public interface AwesomeStorageService {
	Mono<AwesomeStorageDto> getStorageById(String userId, String storageId);

	Flux<AwesomeStorageDto> getStorageByParentStorageId(String userId, String parentStorageId);

	Mono<AwesomeStorageDto> getRootStorageByUserId(String userId);

	Mono<AwesomeStorageDto> makeFolder(String userId, String parentStorageId, String folderName);

	Mono<Tuple2<AwesomeStorage, Resource>> downloadStorage(String userId, String storageId);

	Mono<AwesomeStorageDto> saveStorage(String userId, String parentStorageId, FilePart filePart);

	Mono<Boolean> removeStorageByStorageId(String userId, String storageId);
}
