package com.rational.awesomeproject.service.impl;

import com.rational.awesomeproject.common.enums.StorageExtType;
import com.rational.awesomeproject.repository.AwesomeStorageReactiveRepository;
import com.rational.awesomeproject.repository.AwesomeUserReactiveRepository;
import com.rational.awesomeproject.repository.model.AwesomeStorage;
import com.rational.awesomeproject.service.AwesomeStorageService;
import com.rational.awesomeproject.service.FileStorageService;
import com.rational.awesomeproject.service.dto.AwesomeStorageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class AwesomeStorageServiceImpl implements AwesomeStorageService {
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
	@Transactional
	public Mono<AwesomeStorageDto> saveStorage(String userId, String parentStorageId, FilePart filePart) {

		return alreadyExistStorageNameInParentStorage(userId, parentStorageId, filePart.filename(), StorageExtType.FILE)
				.filter(bool -> !bool)
				// 1. read file size
				.flatMap(bool -> filePart.content().reduce(0L, (size, buffer) -> size + buffer.readableByteCount()))
				// 2. save storage
				.flatMap(fileSize -> storageReactiveRepository.save(AwesomeStorage.makeFile(userId, parentStorageId, filePart.filename(), fileSize)))
				// 3. upload object storage
				.flatMap(storage -> fileStorageService.upload(storage.getId(), filePart)
				                                      // 3. recalculate size
				                                      .flatMap(bool -> recalculateSize(storage)))
				.map(AwesomeStorageDto::of);
	}

	@Override
	@Transactional
	public Mono<Boolean> removeStorageByStorageId(String userId, String storageId) {
		// when try to remove root folder => filtering
		return getAwesomeStorageByUserId(userId, storageId).filter(storage -> storage.getParentStorageId() != null)
		                                                   .flatMap(this::removeStorage)
		                                                   .flatMap(this::recalculateSize)
		                                                   .flatMap(storage -> fileStorageService.delete(storage.getId()));
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

	private Mono<AwesomeStorage> removeStorage(AwesomeStorage awesomeStorage) {
		awesomeStorage.setDeletedAt(OffsetDateTime.now());
		if (awesomeStorage.getExtType() == StorageExtType.FOLDER) {
			return storageReactiveRepository.findAllByParentStorageIdAndDeletedAtIsNull(awesomeStorage.getId())
			                                .flatMap(this::removeStorage)
			                                .then(storageReactiveRepository.save(awesomeStorage));
		}
		return storageReactiveRepository.save(awesomeStorage);
	}

	private Mono<AwesomeStorage> recalculateSize(AwesomeStorage awesomeStorage) {
		Mono<AwesomeStorage> parentStorage = storageReactiveRepository.findById(awesomeStorage.getParentStorageId());
		return storageReactiveRepository.findAllByParentStorageIdAndDeletedAtIsNull(awesomeStorage.getParentStorageId())
		                                .map(AwesomeStorage::getStorageFileSize)
		                                .reduce(0L, Long::sum)
		                                .flatMap(totalSize -> parentStorage.flatMap(storage -> {
			                                storage.setStorageFileSize(totalSize);
			                                return storageReactiveRepository.save(storage);
		                                }));
	}
}
