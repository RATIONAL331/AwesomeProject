package com.rational.awesomeproject.controller;

import com.rational.awesomeproject.controller.dto.AwesomeStorageInfoResponse;
import com.rational.awesomeproject.service.AwesomeStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/storage")
public class AwesomeStorageRestController {
	private final AwesomeStorageService storageService;

	@GetMapping("/{storageId}")
	public ResponseEntity<Mono<AwesomeStorageInfoResponse>> getStorage(@PathVariable String storageId) {
		String userId = "";
		return ResponseEntity.ok(storageService.getStorageById(userId, storageId)
		                                       .map(AwesomeStorageInfoResponse::of));
	}

	@GetMapping("/{storageId}/hierarchy")
	public ResponseEntity<Flux<AwesomeStorageInfoResponse>> getStorageHierarchy(@PathVariable String storageId) {
		String userId = "";
		return ResponseEntity.ok(storageService.getStorageByParentStorageId(userId, storageId)
		                                       .map(AwesomeStorageInfoResponse::of));
	}

	@PostMapping("/new-folder/{storageId}/{folderName}")
	public ResponseEntity<Mono<AwesomeStorageInfoResponse>> makeFolder(@PathVariable String storageId,
	                                                                   @PathVariable String folderName) {
		String userId = "";
		return ResponseEntity.ok(storageService.makeFolder(userId, storageId, folderName)
		                                       .map(AwesomeStorageInfoResponse::of));
	}

	@PostMapping("/{storageId}/upload")
	public ResponseEntity<Mono<AwesomeStorageInfoResponse>> uploadFile(@PathVariable String storageId,
	                                                                   @RequestPart("file") Mono<FilePart> filePartMono) {
		String userId = "";
		return ResponseEntity.ok(filePartMono.flatMap(filePart -> storageService.saveStorage(userId, storageId, filePart))
		                                     .map(AwesomeStorageInfoResponse::of));
	}

	@DeleteMapping("/{storageId}")
	public ResponseEntity<Mono<Boolean>> deleteStorage(@PathVariable String storageId) {
		String userId = "";
		return ResponseEntity.ok(storageService.removeStorageByStorageId(userId, storageId));
	}
}
