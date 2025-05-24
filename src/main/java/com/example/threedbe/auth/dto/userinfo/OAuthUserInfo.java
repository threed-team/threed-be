package com.example.threedbe.auth.dto.userinfo;

// 모든 소셜 로그인 사용자 정보 클래스가 구현해야 하는 인터페이스

public interface OAuthUserInfo {
	String id();

	String email();

	String name();

	String picture();
}