package com.rational.awesomeproject.service.impl;

import com.rational.awesomeproject.common.enums.StorageExtType;
import com.rational.awesomeproject.repository.AwesomeStorageReactiveRepository;
import com.rational.awesomeproject.repository.AwesomeUserReactiveRepository;
import com.rational.awesomeproject.repository.model.AwesomeStorage;
import com.rational.awesomeproject.service.AwesomeStorageService;
import com.rational.awesomeproject.service.FileStorageService;
import com.rational.awesomeproject.service.dto.AwesomeStorageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AwesomeStorageServiceImpl implements AwesomeStorageService {
	private static final long MAX_STORAGE_SIZE = 1024 * 1024 * 1024 * 30L; // 30GB
	private final AwesomeUserReactiveRepository userReactiveRepository;
	private final AwesomeStorageReactiveRepository storageReactiveRepository;
	private final FileStorageService fileStorageService;

	@Override
	public Mono<AwesomeStorageDto> getStorageById(String userId, String storageId) {
		return getAwesomeStorageByUserId(userId, storageId).map(AwesomeStorageDto::of);
	}

	@Override
	public Flux<AwesomeStorageDto> getStorageByParentStorageId(String userId, String parentStorageId) {
		return getAwesomeStorageByUserId(userId, parentStorageId).flatMapMany(storage -> storageReactiveRepository.findAllByParentStorageIdAndDeletedAtIsNull(storage.getId()))
		                                                         .map(AwesomeStorageDto::of);
	}

	@Override
	public Mono<AwesomeStorageDto> getRootStorageByUserId(String userId) {
		return userReactiveRepository.findById(userId)
		                             .flatMap(user -> storageReactiveRepository.findById(user.getRootStorageId()))
		                             .map(AwesomeStorageDto::of);
	}

	@Override
	@Transactional
	public Mono<AwesomeStorageDto> makeFolder(String userId, String parentStorageId, String folderName) {
		return alreadyExistStorageNameInParentStorage(userId, parentStorageId, folderName, StorageExtType.FOLDER)
				.filter(bool -> !bool)
				.flatMap(bool -> storageReactiveRepository.save(AwesomeStorage.makeFolder(userId, parentStorageId, folderName)))
				.map(AwesomeStorageDto::of);
	}

	@Override
	public Mono<Tuple2<AwesomeStorage, Resource>> downloadStorage(String userId, String storageId) {
		Mono<AwesomeStorage> storage = storageReactiveRepository.findByIdAndUserId(storageId, userId)
		                                                        .filter(awesomeStorage -> awesomeStorage.getExtType() == StorageExtType.FILE)
		                                                        .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("Not Exist File/Folder or Only download File"))));
		Mono<InputStreamResource> file = DataBufferUtils.join(fileStorageService.download(userId, storageId))
		                                                .map(dataBuffer -> new InputStreamResource(dataBuffer.asInputStream()));
		return Mono.zip(storage, file);
	}

	@Override
	@Transactional
	public Mono<AwesomeStorageDto> saveStorage(String userId, String parentStorageId, FilePart filePart) {
		return alreadyExistStorageNameInParentStorage(userId, parentStorageId, filePart.filename(), StorageExtType.FILE)
				.filter(bool -> !bool)
				// 1. read file size
				.flatMap(bool -> filePart.content().reduce(0L, (size, buffer) -> size + buffer.readableByteCount()))
				// 2. check storage size
				.flatMap(fileSize -> getRootStorageByUserId(userId).filter(storage -> storage.getStorageFileSize() + fileSize <= MAX_STORAGE_SIZE)
				                                                   .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("Exceed Max Storage Size"))))
				                                                   .thenReturn(fileSize))
				// 3. save storage
				.flatMap(fileSize -> storageReactiveRepository.save(AwesomeStorage.makeFile(userId, parentStorageId, filePart.filename(), fileSize)))
				// 4. upload object storage
				.flatMap(storage -> fileStorageService.upload(userId, storage.getId(), storage.getStorageName(), storage.getStorageFileSize(), filePart)
				                                      // 5. recalculate size
				                                      .flatMap(bool -> recalculateSize(storage)))
				.map(AwesomeStorageDto::of);
	}

	@Override
	@Transactional
	public Mono<Boolean> removeStorageByStorageId(String userId, String storageId) {
		// when try to remove root folder => filtering
		return getAwesomeStorageByUserId(userId, storageId).filter(storage -> storage.getParentStorageId() != null)
		                                                   .flatMap(awesomeStorage -> removeStorage(userId, awesomeStorage))
		                                                   .flatMap(this::recalculateSize)
		                                                   .thenReturn(true);
	}

	private Mono<AwesomeStorage> getAwesomeStorageByUserId(String userId, String storageId) {
		return storageReactiveRepository.findByIdAndUserId(storageId, userId)
		                                .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("Not Exist File/Folder"))));
	}

	private Mono<Boolean> alreadyExistStorageNameInParentStorage(String userId, String parentStorageId, String storageName, StorageExtType extType) {
		return getAwesomeStorageByUserId(userId, parentStorageId)
				.flatMapMany(storage -> storageReactiveRepository.findAllByParentStorageIdAndDeletedAtIsNull(storage.getId()))
				.filter(storage -> extType.equals(storage.getExtType()))
				.filter(storage -> storage.getStorageName().equals(storageName))
				.hasElements();
	}

	private Mono<AwesomeStorage> removeStorage(String userId, AwesomeStorage awesomeStorage) {
		awesomeStorage.setDeletedAt(LocalDateTime.now());
		// if storage is folder, remove all child folder and file
		if (awesomeStorage.getExtType() == StorageExtType.FOLDER) {
			return storageReactiveRepository.findAllByParentStorageIdAndDeletedAtIsNull(awesomeStorage.getId())
			                                .flatMap((storage) -> removeStorage(userId, storage))
			                                .then(storageReactiveRepository.save(awesomeStorage));
		}

		// if storage is file, just remove
		return fileStorageService.delete(userId, awesomeStorage.getId())
		                         .flatMap(bool -> storageReactiveRepository.save(awesomeStorage));
	}

	private Mono<AwesomeStorage> recalculateSize(AwesomeStorage awesomeStorage) {
		// if root folder calculate itself
		if (awesomeStorage.getParentStorageId() == null) {
			return calculateStorage(awesomeStorage.getId(), awesomeStorage);
		}

		// if not root folder calculate parent folder and recursion
		return storageReactiveRepository.findById(awesomeStorage.getParentStorageId())
		                                .flatMap(parentStorage -> calculateStorage(parentStorage.getId(), parentStorage))
		                                .flatMap(this::recalculateSize)
		                                // then return original storage
		                                .thenReturn(awesomeStorage);
	}

	private Mono<AwesomeStorage> calculateStorage(String storageId, AwesomeStorage storage) {
		return storageReactiveRepository.findAllByParentStorageIdAndDeletedAtIsNull(storageId)
		                                .map(AwesomeStorage::getStorageFileSize)
		                                .reduce(0L, Long::sum)
		                                .flatMap(totalSize -> {
			                                storage.setStorageFileSize(totalSize);
			                                return storageReactiveRepository.save(storage);
		                                });
	}
}
