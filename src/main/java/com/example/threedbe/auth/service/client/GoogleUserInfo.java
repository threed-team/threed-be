package com.example.threedbe.auth.service.client;

// record 문법으로 사용자 정보 담기
public record GoogleUserInfo(
	String id,
	String email,
	String name,
	String picture
) {
}
