package com.rational.awesomeproject.service;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FileStorageService {
	Flux<DataBuffer> download(String userId, String fileId);

	Mono<Boolean> upload(String userId, String fileId, String fileName, long fileSize, FilePart filePart);

	Mono<Boolean> delete(String userId, String fileId);
}
