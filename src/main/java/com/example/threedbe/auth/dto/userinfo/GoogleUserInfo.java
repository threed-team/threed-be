package com.example.threedbe.auth.dto.userinfo;

public record GoogleUserInfo(
	String id,
	String email,
	String name,
	String picture
) implements OAuthUserInfo {
}