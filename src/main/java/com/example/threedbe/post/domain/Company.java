package com.example.threedbe.post.domain;

public enum Company {

	NAVER("네이버"),

	KAKAO("카카오"),

	DEVOCEAN("데보션"),

	TOSS("토스"),

	MY_REAL_TRIP("마이리얼트립"),

	LINE("라인"),

	DAANGN("당근마켓"),

	ETC("기타");

	private final String value;

	Company(String value) {
		this.value = value;
	}

}
