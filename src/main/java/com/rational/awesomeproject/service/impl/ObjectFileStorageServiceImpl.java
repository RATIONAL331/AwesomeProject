package com.rational.awesomeproject.service.impl;

import com.rational.awesomeproject.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ObjectFileStorageServiceImpl implements FileStorageService {
	private final WebClient webClient;

	@Override
	public Mono<Boolean> upload(String fileId, FilePart filePart) {
		log.info("upload file to object storage fileId: {}", fileId);

		return Mono.just(true);
	}

	@Override
	public Mono<Boolean> delete(String fileId) {
		log.info("delete file from object storage fileId: {}", fileId);

		return Mono.just(true);
	}
}
