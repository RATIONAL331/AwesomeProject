package com.rational.awesomeproject.controller;

import com.rational.awesomeproject.controller.dto.AwesomeStorageInfoResponse;
import com.rational.awesomeproject.repository.model.AwesomeUser;
import com.rational.awesomeproject.service.AwesomeStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/storage")
public class AwesomeStorageRestController {
	private final AwesomeStorageService storageService;

	@GetMapping("/{storageId}")
	public Mono<AwesomeStorageInfoResponse> getStorage(@PathVariable String storageId) {
		return ReactiveSecurityContextHolder.getContext()
		                                    .map(securityContext -> securityContext.getAuthentication().getPrincipal())
		                                    .cast(AwesomeUser.class)
		                                    .map(AwesomeUser::getId)
		                                    .flatMap(userId -> storageService.getStorageById(userId, storageId))
		                                    .map(AwesomeStorageInfoResponse::of);
	}

	@PostMapping("/{storageId}")
	public Mono<AwesomeStorageInfoResponse> uploadFile(@PathVariable String storageId,
	                                                   @RequestPart("file") Mono<FilePart> filePartMono) {
		return ReactiveSecurityContextHolder.getContext()
		                                    .map(securityContext -> securityContext.getAuthentication().getPrincipal())
		                                    .cast(AwesomeUser.class)
		                                    .map(AwesomeUser::getId)
		                                    .flatMap(userId -> filePartMono.flatMap(filePart -> storageService.saveStorage(userId, storageId, filePart)))
		                                    .map(AwesomeStorageInfoResponse::of);
	}

	@DeleteMapping("/{storageId}")
	public Mono<Boolean> deleteStorage(@PathVariable String storageId) {
		return ReactiveSecurityContextHolder.getContext()
		                                    .map(securityContext -> securityContext.getAuthentication().getPrincipal())
		                                    .cast(AwesomeUser.class)
		                                    .map(AwesomeUser::getId)
		                                    .flatMap(userId -> storageService.removeStorageByStorageId(userId, storageId));
	}

	@GetMapping("/{storageId}/hierarchy")
	public Flux<AwesomeStorageInfoResponse> getStorageHierarchy(@PathVariable String storageId) {
		return ReactiveSecurityContextHolder.getContext()
		                                    .map(securityContext -> securityContext.getAuthentication().getPrincipal())
		                                    .cast(AwesomeUser.class)
		                                    .map(AwesomeUser::getId)
		                                    .flatMapMany(userId -> storageService.getStorageByParentStorageId(userId, storageId))
		                                    .map(AwesomeStorageInfoResponse::of);
	}

	@PostMapping("/new-folder/{storageId}/{folderName}")
	public Mono<AwesomeStorageInfoResponse> makeFolder(@PathVariable String storageId,
	                                                   @PathVariable String folderName) {
		return ReactiveSecurityContextHolder.getContext()
		                                    .map(securityContext -> securityContext.getAuthentication().getPrincipal())
		                                    .cast(AwesomeUser.class)
		                                    .map(AwesomeUser::getId)
		                                    .flatMap(userId -> storageService.makeFolder(userId, storageId, folderName))
		                                    .map(AwesomeStorageInfoResponse::of);
	}
}
