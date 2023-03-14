package com.rational.awesomeproject.service.impl;

import com.rational.awesomeproject.repository.AwesomeStorageReactiveRepository;
import com.rational.awesomeproject.service.AwesomeStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AwesomeStorageServiceImpl implements AwesomeStorageService {
	private final AwesomeStorageReactiveRepository storageReactiveRepository;
}
