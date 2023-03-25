package com.rational.awesomeproject.controller;

import com.rational.awesomeproject.controller.dto.AwesomeStorageInfoResponse;
import com.rational.awesomeproject.service.AwesomeStorageService;
import com.rational.awesomeproject.service.auth.dto.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/storage")
public class AwesomeStorageRestController {
	private final AwesomeStorageService storageService;

	@GetMapping("/{storageId}")
	public Mono<AwesomeStorageInfoResponse> getStorage(@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
	                                                   @PathVariable String storageId) {
		String userId = customUserPrincipal.getUser().getId();
		return storageService.getStorageById(userId, storageId)
		                     .map(AwesomeStorageInfoResponse::of);
	}

	@PostMapping("/{storageId}")
	public Mono<AwesomeStorageInfoResponse> uploadFile(@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
	                                                   @PathVariable String storageId,
	                                                   @RequestPart("file") Mono<FilePart> filePartMono) {
		String userId = customUserPrincipal.getUser().getId();
		return filePartMono.flatMap(filePart -> storageService.saveStorage(userId, storageId, filePart))
		                   .map(AwesomeStorageInfoResponse::of);
	}

	@DeleteMapping("/{storageId}")
	public Mono<Boolean> deleteStorage(@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
	                                   @PathVariable String storageId) {
		String userId = customUserPrincipal.getUser().getId();
		return storageService.removeStorageByStorageId(userId, storageId);
	}

	@GetMapping("/{storageId}/hierarchy")
	public Flux<AwesomeStorageInfoResponse> getStorageHierarchy(@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
	                                                            @PathVariable String storageId) {
		String userId = customUserPrincipal.getUser().getId();
		return storageService.getStorageByParentStorageId(userId, storageId)
		                     .map(AwesomeStorageInfoResponse::of);
	}

	@PostMapping("/new-folder/{storageId}/{folderName}")
	public Mono<AwesomeStorageInfoResponse> makeFolder(@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
	                                                   @PathVariable String storageId,
	                                                   @PathVariable String folderName) {
		String userId = customUserPrincipal.getUser().getId();
		return storageService.makeFolder(userId, storageId, folderName)
		                     .map(AwesomeStorageInfoResponse::of);
	}
}
