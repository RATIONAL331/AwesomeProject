package com.rational.awesomeproject.service;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface FileStorageService {
	Mono<Boolean> upload(String fileName, FilePart filePart);

	Mono<Boolean> delete(String fileId);
}
