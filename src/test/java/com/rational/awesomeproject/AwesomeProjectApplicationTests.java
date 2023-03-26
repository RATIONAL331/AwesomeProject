package com.rational.awesomeproject;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AwesomeProjectApplicationTests {

	@Autowired
	private StringEncryptor encryptor;

}
