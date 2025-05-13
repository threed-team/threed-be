package com.example.threedbe.auth.domain;

import java.util.Date;

import com.example.threedbe.auth.config.AuthProperties;
import com.example.threedbe.common.exception.ThreedBadRequestException;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;

@Getter
public class AccessToken implements JwtToken {

	private static final String ACCESS_TOKEN_PREFIX = "Bearer";

	private final String value;

	public AccessToken(long memberId, AuthProperties authProperties) {
		Date validity = new Date(System.currentTimeMillis() + authProperties.getAccessExpiration());
		this.value = Jwts.builder()
			.setSubject(String.valueOf(memberId))
			.setExpiration(validity)
			.signWith(SignatureAlgorithm.HS256, authProperties.getAccessKey())
			.compact();
	}

	public AccessToken(String rawValue) {
		validate(rawValue);
		this.value = parseAccessToken(rawValue);
	}

	private void validate(String value) {
		if (value == null || !value.startsWith(ACCESS_TOKEN_PREFIX)) {
			throw new ThreedBadRequestException("잘못된 액세스 토큰 형식입니다.");
		}
	}

	private String parseAccessToken(String rawValue) {
		return rawValue.substring(ACCESS_TOKEN_PREFIX.length()).trim();
	}

	@Override
	public String getSecretKey(AuthProperties authProperties) {
		return authProperties.getAccessKey();
	}

}
