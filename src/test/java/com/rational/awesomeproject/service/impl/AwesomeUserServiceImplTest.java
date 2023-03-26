package com.rational.awesomeproject.service.impl;

import com.rational.awesomeproject.repository.AwesomeStorageReactiveRepository;
import com.rational.awesomeproject.repository.AwesomeUserReactiveRepository;
import com.rational.awesomeproject.repository.model.AwesomeStorage;
import com.rational.awesomeproject.repository.model.AwesomeUser;
import com.rational.awesomeproject.service.dto.AwesomeUserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class AwesomeUserServiceImplTest {
	@InjectMocks
	private AwesomeUserServiceImpl awesomeUserService;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private AwesomeUserReactiveRepository userReactiveRepository;
	@Mock
	private AwesomeStorageReactiveRepository storageReactiveRepository;

	private final AwesomeUser mockUser = AwesomeUser.makeDefault("name", "pwd");
	private final AwesomeStorage mockStorage = AwesomeStorage.makeDefault("1");

	@BeforeEach
	public void beforeEach() {
		mockUser.setId("1");
		mockUser.setRootStorageId("root");
		Mockito.lenient()
		       .when(userReactiveRepository.findById(Mockito.anyString()))
		       .thenReturn(Mono.just(mockUser));

		Mockito.lenient()
		       .when(userReactiveRepository.save(Mockito.any(AwesomeUser.class)))
		       .thenReturn(Mono.just(mockUser));

		mockStorage.setId("root");
		Mockito.lenient()
		       .when(storageReactiveRepository.save(Mockito.any(AwesomeStorage.class)))
		       .thenReturn(Mono.just(mockStorage));
	}

	@Test
	@DisplayName("유저 생성하기 테스트")
	void createUser() {
		Mockito.when(userReactiveRepository.findByUsername(Mockito.anyString()))
		       .thenReturn(Mono.empty());
		awesomeUserService.createUser("name", "pwd")
		                  .as(StepVerifier::create)
		                  .expectNextMatches(userDto -> userDto.getRootStorageId().equals("root"))
		                  .verifyComplete();

		Mockito.verify(userReactiveRepository, Mockito.times(2))
		       .save(Mockito.any(AwesomeUser.class));
	}

	@Test
	@DisplayName("이미 있는 유저 생성하기 테스트")
	void createUserWithExistingUser() {
		Mockito.when(userReactiveRepository.findByUsername(Mockito.anyString()))
		       .thenReturn(Mono.just(mockUser));

		awesomeUserService.createUser("name", "pwd")
		                  .as(StepVerifier::create)
		                  .expectNextMatches(userDto -> userDto.getRootStorageId().equals("root"))
		                  .verifyComplete();

		Mockito.verify(userReactiveRepository, Mockito.never())
		       .save(Mockito.any(AwesomeUser.class));
	}

	@Test
	@DisplayName("유저 아이디로 유저 찾기 테스트")
	void getUserByUserId() {
		Mono<AwesomeUserDto> userByUserId = awesomeUserService.getUserByUserId("1");
		userByUserId.as(StepVerifier::create)
		            .expectNextMatches(userDto -> userDto.getUsername().equals("name"))
		            .verifyComplete();
	}

	@Test
	@DisplayName("유저 이름으로 유저 찾기 테스트")
	void getUserByUsername() {
		Mockito.when(userReactiveRepository.findByUsername(Mockito.anyString()))
		       .thenReturn(Mono.just(mockUser));
		Mono<AwesomeUserDto> userByUsername = awesomeUserService.getUserByUsername("name");
		userByUsername.as(StepVerifier::create)
		              .expectNextMatches(userDto -> userDto.getRootStorageId().equals("root"))
		              .verifyComplete();
	}
}