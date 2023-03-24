package com.rational.awesomeproject.service.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

@Service
public class JwtService {
	private static final SignatureAlgorithm HS256 = SignatureAlgorithm.HS256;
	private static final long accessTokenValidTime = 1000L * 60L * 60L * 24L; // 24시간
	private static final long refreshTokenValidTime = 1000L * 60L * 60L * 24L; // 24시간

	private final SecretKey secretKey;
	private final JwtParser parser;

	public JwtService(@Value("${jwtSecretKey}") String secretKey) {
		byte[] encodedKey = Decoders.BASE64.decode(secretKey);
		this.secretKey = new SecretKeySpec(encodedKey, HS256.getJcaName());
		this.parser = Jwts.parserBuilder()
		                  .setSigningKey(this.secretKey)
		                  .build();
	}

	public Boolean validateToken(String token) {
		try {
			this.parser.parseClaimsJws(token)
			           .getBody();
			return true;
		} catch (Exception e) {
			throw new RuntimeException("인증 실패");
		}
	}

	public Mono<String> generateAccessToken(String uid) {
		Claims claims = Jwts.claims().setSubject("accessToken");
		claims.put("uid", uid);
		Date currentTime = new Date();
		return Mono.just(
				Jwts.builder()
				    .setClaims(claims)
				    .setIssuedAt(currentTime)
				    .setExpiration(new Date(currentTime.getTime() + accessTokenValidTime))
				    .signWith(secretKey, HS256)
				    .compact());
	}

	public Mono<String> generateRefreshToken(String uid) {
		Claims claims = Jwts.claims().setSubject("refreshToken");
		claims.put("uid", uid);
		Date currentTime = new Date();
		return Mono.just(
				Jwts.builder()
				    .setClaims(claims)
				    .setIssuedAt(currentTime)
				    .setExpiration(new Date(currentTime.getTime() + refreshTokenValidTime))
				    .signWith(secretKey, HS256)
				    .compact());
	}

	public String getUidFromToken(String token) {
		return this.parser.parseClaimsJws(token)
		                  .getBody()
		                  .get("uid", String.class);
	}
}