package com.rational.awesomeproject.service.impl;

import com.rational.awesomeproject.common.enums.StorageExtType;
import com.rational.awesomeproject.repository.AwesomeStorageReactiveRepository;
import com.rational.awesomeproject.repository.AwesomeUserReactiveRepository;
import com.rational.awesomeproject.service.AwesomeStorageService;
import com.rational.awesomeproject.service.dto.AwesomeStorageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AwesomeStorageServiceImpl implements AwesomeStorageService {
	private final AwesomeUserReactiveRepository userReactiveRepository;
	private final AwesomeStorageReactiveRepository storageReactiveRepository;

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
		return null;
	}

	@Override
	@Transactional
	public Mono<AwesomeStorageDto> saveStorage(String userId, String parentStorageId, FilePart filePart) {
		return null;
	}

	@Override
	@Transactional
	public Mono<Void> removeStorage(String userId, String storageId) {
		return alreadyExistStorage(userId, storageId).filter(bool -> bool)
		                                             .flatMap(bool -> storageReactiveRepository.deleteById(storageId));
	}

	@Override
	public Mono<Boolean> alreadyExistStorage(String userId, String storageId) {
		return storageReactiveRepository.findByIdAndUserId(storageId, userId)
		                                .hasElement();
	}

	@Override
	public Mono<Boolean> alreadyExistStorageNameInParentStorage(String userId, String parentStorageId, String storageName, StorageExtType extType) {
		return storageReactiveRepository.findByIdAndUserId(parentStorageId, userId)
		                                .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("Not Exist"))))
		                                .flatMapMany(storage -> storageReactiveRepository.findAllByParentStorageIdAndDeletedAtIsNull(storage.getId()))
		                                .filter(storage -> extType.equals(storage.getExtType()))
		                                .filter(storage -> storage.getStorageName().equals(storageName))
		                                .hasElements();
	}
}
