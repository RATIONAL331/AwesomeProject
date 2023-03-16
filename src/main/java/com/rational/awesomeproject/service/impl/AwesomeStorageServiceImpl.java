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
		return storageReactiveRepository.findById(storageId)
		                                .filter(storage -> userId.equals(storage.getUserId()))
		                                .map(AwesomeStorageDto::of);
	}

	@Override
	public Flux<AwesomeStorageDto> getStorageByParentStorageId(String userId, String parentStorageId) {
		return storageReactiveRepository.findAllByParentStorageIdAndDeletedAtIsNull(parentStorageId)
		                                .filter(storage -> userId.equals(storage.getUserId()))
		                                .map(AwesomeStorageDto::of);
	}

	@Override
	public Flux<AwesomeStorageDto> getStorageByUserId(String userId) {
		return storageReactiveRepository.findAllByUserIdAndDeletedAtIsNull(userId)
		                                .map(AwesomeStorageDto::of);
	}

	@Override
	public Flux<AwesomeStorageDto> getStorageByUsername(String username) {
		return userReactiveRepository.findByUsername(username)
		                             .flatMapMany(user -> storageReactiveRepository.findAllByUserIdAndDeletedAtIsNull(user.getId()))
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
		/**
		 * todo
		 * 1. upload object storage
		 * 2. save file info
		 * 3. recalculate storage size
		 */
		return null;
	}

	@Override
	@Transactional
	public Mono<Boolean> removeStorageByStorageId(String userId, String storageId) {
		return storageReactiveRepository.findByIdAndUserId(storageId, userId)
		                                .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("Not Exist File/Folder"))))
		                                .flatMap(this::removeStorage)
		                                .flatMap(this::recalculateSize)
		                                .flatMap(storage -> fileStorageService.delete(storage.getId()));
	}

	private Mono<Boolean> alreadyExistStorageNameInParentStorage(String userId, String parentStorageId, String storageName, StorageExtType extType) {
		return storageReactiveRepository.findByIdAndUserId(parentStorageId, userId)
		                                .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("Not Exist File/Folder"))))
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
