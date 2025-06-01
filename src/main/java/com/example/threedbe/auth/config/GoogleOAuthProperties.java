package com.example.threedbe.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;

@Getter
@ConfigurationProperties(prefix = "oauth.google")
public class GoogleOAuthProperties {

	private final String clientId;
	private final String clientSecret;
	private final String redirectUri;

	public GoogleOAuthProperties(String clientId, String clientSecret, String redirectUri) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.redirectUri = redirectUri;
	}
}
