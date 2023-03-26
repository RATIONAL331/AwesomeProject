package com.rational.awesomeproject.service.impl;

import com.rational.awesomeproject.repository.AwesomeStorageReactiveRepository;
import com.rational.awesomeproject.repository.AwesomeUserReactiveRepository;
import com.rational.awesomeproject.repository.model.AwesomeStorage;
import com.rational.awesomeproject.repository.model.AwesomeUser;
import com.rational.awesomeproject.service.FileStorageService;
import com.rational.awesomeproject.service.dto.AwesomeStorageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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

	@BeforeEach
	public void setUp() {
		AwesomeUser data = AwesomeUser.makeDefault("name", "pwd");
		data.setId("1");
		data.setRootStorageId("1");
		Mockito.lenient()
		       .when(userReactiveRepository.findById(Mockito.anyString()))
		       .thenReturn(Mono.just(data));
	}

	@Test
	@DisplayName("폴더 생성하기 테스트 - 일반적인 경우")
	void makeFolder() {
		AwesomeStorage rootStorage = AwesomeStorage.makeDefault("1");
		rootStorage.setId("1");

		Mockito.when(storageReactiveRepository.findByIdAndUserId(Mockito.anyString(), Mockito.anyString()))
		       .thenReturn(Mono.just(rootStorage));
		Mockito.when(storageReactiveRepository.findAllByParentStorageIdAndDeletedAtIsNull(Mockito.anyString()))
		       .thenReturn(Flux.just(
				       AwesomeStorage.makeFolder("1", "1", "test"),
				       AwesomeStorage.makeFolder("1", "1", "test2"),
				       AwesomeStorage.makeFile("1", "1", "test3", 1L)));

		Mockito.when(storageReactiveRepository.save(Mockito.any(AwesomeStorage.class)))
		       .thenReturn(Mono.just(AwesomeStorage.makeFolder("1", "1", "test4")));

		Mono<AwesomeStorageDto> test = awesomeStorageService.makeFolder("1", "1", "test4");
		test.as(StepVerifier::create)
		    .expectNextMatches(awesomeStorageDto -> awesomeStorageDto.getStorageName().equals("test4"))
		    .verifyComplete();
	}

	@Test
	@DisplayName("폴더 생성하기 테스트 - 중복된 이름의 폴더를 생성하는 경우")
	void makeFolderDup() {
		AwesomeStorage rootStorage = AwesomeStorage.makeDefault("1");
		rootStorage.setId("1");

		Mockito.when(storageReactiveRepository.findByIdAndUserId(Mockito.anyString(), Mockito.anyString()))
		       .thenReturn(Mono.just(rootStorage));
		Mockito.when(storageReactiveRepository.findAllByParentStorageIdAndDeletedAtIsNull(Mockito.anyString()))
		       .thenReturn(Flux.just(
				       AwesomeStorage.makeFolder("1", "1", "test"),
				       AwesomeStorage.makeFolder("1", "1", "test2"),
				       AwesomeStorage.makeFile("1", "1", "test3", 1L)));

		Mono<AwesomeStorageDto> test = awesomeStorageService.makeFolder("1", "1", "test");
		test.as(StepVerifier::create)
		    .verifyComplete();

		Mockito.verify(storageReactiveRepository, Mockito.never()).save(Mockito.any(AwesomeStorage.class));
	}


	@Test
	@DisplayName("폴더 생성하기 테스트 - 사용자에게 없는 폴더를 상대로 생성하는 경우")
	void makeFolderNotAuthor() {
		Mockito.when(storageReactiveRepository.findByIdAndUserId(Mockito.anyString(), Mockito.anyString()))
		       .thenReturn(Mono.empty());

		Mono<AwesomeStorageDto> test = awesomeStorageService.makeFolder("1", "1", "test4");
		test.as(StepVerifier::create)
		    .expectErrorMessage("Not Exist File/Folder")
		    .verify();
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
		AwesomeUser data = AwesomeUser.makeDefault("name", "pwd");
		data.setRootStorageId("12345");
		Mockito.when(userReactiveRepository.findById(Mockito.anyString()))
		       .thenReturn(Mono.just(data));

		AwesomeStorage rootStorage = AwesomeStorage.makeDefault("1");
		rootStorage.setId("12345");
		Mockito.when(storageReactiveRepository.findById("12345"))
		       .thenReturn(Mono.just(rootStorage));

		awesomeStorageService.getRootStorageByUserId("1")
		                     .as(StepVerifier::create)
		                     .expectNextMatches(awesomeStorageDto -> awesomeStorageDto.getId().equals("12345"))
		                     .verifyComplete();
	}

	@Test
	@DisplayName("폴더 내용 가져오기 테스트")
	void getFolderContent() {
		AwesomeStorage rootStorage = AwesomeStorage.makeDefault("1");
		rootStorage.setId("1");

		Mockito.when(storageReactiveRepository.findByIdAndUserId(Mockito.anyString(), Mockito.anyString()))
		       .thenReturn(Mono.just(rootStorage));
		Mockito.when(storageReactiveRepository.findAllByParentStorageIdAndDeletedAtIsNull(Mockito.anyString()))
		       .thenReturn(Flux.just(
				       AwesomeStorage.makeFolder("1", "1", "test"),
				       AwesomeStorage.makeFolder("1", "1", "test2"),
				       AwesomeStorage.makeFile("1", "1", "test3", 1L)));

		awesomeStorageService.getStorageByParentStorageId("1", "1")
		                     .as(StepVerifier::create)
		                     .expectNextCount(3)
		                     .verifyComplete();
	}

	@Test
	@DisplayName("폴더 정보 가져오기 테스트")
	void getFolderInfo() {
		AwesomeStorage rootStorage = AwesomeStorage.makeDefault("1");
		rootStorage.setId("1");

		Mockito.when(storageReactiveRepository.findByIdAndUserId(Mockito.anyString(), Mockito.anyString()))
		       .thenReturn(Mono.just(rootStorage));

		awesomeStorageService.getStorageById("1", "1")
		                     .as(StepVerifier::create)
		                     .expectNextMatches(awesomeStorageDto -> awesomeStorageDto.getId().equals("1"))
		                     .verifyComplete();
	}
}