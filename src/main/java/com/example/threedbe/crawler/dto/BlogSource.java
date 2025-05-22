package com.example.threedbe.crawler.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BlogSource {

	NAVER("네이버", "https://d2.naver.com/d2.atom"),

	KAKAO_TECH("카카오 기술 블로그", "https://tech.kakao.com/blog"),

	LINE_ENGINEERING("라인 엔지니어링", "https://engineering.linecorp.com/ko/blog"),

	TOSS_TECH("토스 기술 블로그", "https://toss.tech");

	private final String displayName;

	private final String baseUrl;

}
