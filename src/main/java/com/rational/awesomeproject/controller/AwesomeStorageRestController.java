package com.rational.awesomeproject.controller;

import com.rational.awesomeproject.controller.dto.AwesomeStorageInfoResponse;
import com.rational.awesomeproject.repository.model.AwesomeStorage;
import com.rational.awesomeproject.service.AwesomeStorageService;
import com.rational.awesomeproject.service.auth.dto.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

	@GetMapping("/{storageId}/download")
	public Mono<Resource> downloadStorage(@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
	                                      @PathVariable String storageId,
	                                      ServerHttpResponse response) {
		String userId = customUserPrincipal.getUser().getId();
		Mono<Tuple2<AwesomeStorage, Resource>> tuple2Mono = storageService.downloadStorage(userId, storageId);

		return tuple2Mono.flatMap(tuple2 -> {
			AwesomeStorage t1 = tuple2.getT1();
			Resource t2 = tuple2.getT2();

			HttpHeaders headers = response.getHeaders();
			headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename*=utf-8''" + encodeFileName(t1.getStorageName()));
			headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
			headers.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(t1.getStorageFileSize()));
			return Mono.just(t2);
		});
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

	private static String encodeFileName(String fileName) {
		try {
			return URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
		} catch (Exception e) {
			return fileName;
		}
	}
}
