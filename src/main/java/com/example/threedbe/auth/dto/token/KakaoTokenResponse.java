package com.example.threedbe.auth.dto.token;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoTokenResponse(
	@JsonProperty("access_token") String accessToken,
	@JsonProperty("token_type") String tokenType,
	@JsonProperty("refresh_token") String refreshToken,
	@JsonProperty("expires_in") int expiresIn,
	@JsonProperty("scope") String scope
) {
}
