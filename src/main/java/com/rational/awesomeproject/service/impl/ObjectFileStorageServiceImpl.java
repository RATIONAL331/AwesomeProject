package com.rational.awesomeproject.service.impl;

import com.rational.awesomeproject.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ObjectFileStorageServiceImpl implements FileStorageService {
	private final WebClient webClient;

	@Override
	public Mono<Boolean> upload(String fileId, FilePart filePart) {
		return Mono.empty();
	}

	@Override
	public Mono<Boolean> delete(String fileId) {
		return Mono.empty();
	}
}
