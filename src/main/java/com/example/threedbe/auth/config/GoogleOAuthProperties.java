package com.example.threedbe.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
@ConfigurationProperties(prefix = "oauth.google")
public class GoogleOAuthProperties {

	private String clientId;
	private String clientSecret;
	private String redirectUri;

	// setter는 ConfigurationProperties 바인딩을 위해 반드시 필요합니다
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}
}
