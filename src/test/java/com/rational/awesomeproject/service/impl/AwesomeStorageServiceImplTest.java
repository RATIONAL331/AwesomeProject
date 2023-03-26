package com.rational.awesomeproject.service.impl;

import com.rational.awesomeproject.repository.AwesomeStorageReactiveRepository;
import com.rational.awesomeproject.repository.AwesomeUserReactiveRepository;
import com.rational.awesomeproject.service.FileStorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AwesomeStorageServiceImplTest {
	@InjectMocks
	private AwesomeStorageServiceImpl awesomeStorageService;
	@Mock
	private AwesomeUserReactiveRepository userReactiveRepository;
	@Mock
	private AwesomeStorageReactiveRepository storageReactiveRepository;
	@Mock
	private FileStorageService fileStorageService;

	@Test
	@DisplayName("폴더 생성하기 테스트")
	void makeFolder() {
	}

	@Test
	@DisplayName("파일 업로드하기 테스트")
	void uploadFile() {
	}

	@Test
	@DisplayName("파일 다운로드하기 테스트")
	void downloadFile() {
	}

	@Test
	@DisplayName("파일 삭제하기 테스트")
	void deleteFile() {
	}

	@Test
	@DisplayName("폴더 삭제하기 테스트")
	void deleteFolder() {
	}

	@Test
	@DisplayName("최상위 폴더 가져오기 테스트")
	void getRootFolder() {
	}

	@Test
	@DisplayName("폴더 내용 가져오기 테스트")
	void getFolderContent() {
	}

	@Test
	@DisplayName("폴더 정보 가져오기 테스트")
	void getFolderInfo() {
	}
}