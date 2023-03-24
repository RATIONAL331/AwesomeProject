package com.rational.awesomeproject.service.auth.dto;

import com.rational.awesomeproject.repository.model.AwesomeUser;
import lombok.Getter;
import org.springframework.security.core.userdetails.User;

@Getter
public class CustomUserPrincipal extends User {
	private final AwesomeUser user;

	public CustomUserPrincipal(AwesomeUser user) {
		super(user.getId(), user.getPassword(), user.getAuthorities());
		this.user = user;
	}
}