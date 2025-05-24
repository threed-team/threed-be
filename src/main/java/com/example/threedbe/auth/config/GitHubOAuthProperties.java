package com.example.threedbe.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;

@Getter
@ConfigurationProperties(prefix = "oauth.github")
public class GitHubOAuthProperties {

	private final String clientId;
	private final String clientSecret;
	private final String redirectUri;

	public GitHubOAuthProperties(String clientId, String clientSecret, String redirectUri) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.redirectUri = redirectUri;
	}
}
