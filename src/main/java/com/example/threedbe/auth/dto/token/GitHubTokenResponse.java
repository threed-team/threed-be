package com.example.threedbe.auth.dto.token;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GitHubTokenResponse(
	@JsonProperty("access_token") String accessToken,
	@JsonProperty("token_type") String tokenType,
	@JsonProperty("scope") String scope
) {
}
