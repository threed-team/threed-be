package com.example.threedbe.auth.dto.userinfo;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GitHubUserInfo(
	@JsonProperty("id") String id,
	@JsonProperty("login") String login,
	@JsonProperty("avatar_url") String avatarUrl
) implements OAuthUserInfo {

	@Override
	public String email() {
		return login + "@github.com";
	}

	@Override
	public String name() {
		return login;
	}

	@Override
	public String picture() {
		return avatarUrl;
	}
}