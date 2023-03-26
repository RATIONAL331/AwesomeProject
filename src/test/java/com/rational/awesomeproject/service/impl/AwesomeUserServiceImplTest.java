package com.rational.awesomeproject.service.impl;

import com.rational.awesomeproject.repository.AwesomeStorageReactiveRepository;
import com.rational.awesomeproject.repository.AwesomeUserReactiveRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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


	@Test
	@DisplayName("유저 생성하기 테스트")
	void createUser() {

	}

	@Test
	@DisplayName("이미 있는 유저 생성하기 테스트")
	void createUserWithExistingUser() {

	}

	@Test
	@DisplayName("유저 아이디로 유저 찾기 테스트")
	void getUserByUserId() {

	}

	@Test
	@DisplayName("유저 이름으로 유저 찾기 테스트")
	void getUserByUsername() {

	}
}