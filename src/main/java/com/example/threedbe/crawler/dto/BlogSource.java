package com.example.threedbe.crawler.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BlogSource {

	NAVER("네이버", "https://d2.naver.com/d2.atom"),

	KAKAO("카카오", "https://tech.kakao.com/feed");

	private final String displayName;

	private final String baseUrl;

}
