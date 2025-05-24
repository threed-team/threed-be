package com.example.threedbe.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;

@Getter
@ConfigurationProperties(prefix = "oauth.kakao")
public class KakaoOAuthProperties {

	private final String clientId;
	private final String redirectUri;

	public KakaoOAuthProperties(String clientId, String redirectUri) {
		this.clientId = clientId;
		this.redirectUri = redirectUri;
	}
}
