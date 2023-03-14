package com.rational.awesomeproject.repository.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Document
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AwesomeUser implements UserDetails {
	@Id
	private String id;

	private String username;
	private String rootStorageId;

	@CreatedDate
	private OffsetDateTime createdAt;

	@LastModifiedDate
	private OffsetDateTime updatedAt;

	private String password;
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;

	private List<String> permissions = new ArrayList<>();

	public static AwesomeUser makeDefault(String username, String password) {
		AwesomeUser awesomeUser = new AwesomeUser();
		awesomeUser.setUsername(username);
		awesomeUser.setPassword(password);
		awesomeUser.setEnabled(true);
		awesomeUser.setAccountNonExpired(true);
		awesomeUser.setAccountNonLocked(true);
		awesomeUser.setCredentialsNonExpired(true);
		awesomeUser.setPermissions(Collections.emptyList());

		OffsetDateTime now = OffsetDateTime.now();
		awesomeUser.setCreatedAt(now);
		awesomeUser.setUpdatedAt(now);
		return awesomeUser;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return permissions.stream()
		                  .map(SimpleGrantedAuthority::new)
		                  .collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return this.accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.accountNonExpired;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return this.credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}
}
