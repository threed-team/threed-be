package com.example.threedbe.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

	private final String accessKey;

	private final String refreshKey;

	private final long accessExpiration;

	private final long refreshExpiration;

}
