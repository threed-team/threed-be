package com.example.threedbe.post.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Company {

	NAVER("네이버"),

	KAKAO("카카오"),

	DEVOCEAN("데보션"),

	TOSS("토스"),

	MY_REAL_TRIP("마이리얼트립"),

	LINE("라인"),

	DAANGN("당근마켓"),

	OLIVE_YOUNG("올리브영"),

	ETC("기타");

	private final String value;

	public static final List<Company> MAIN_COMPANIES
		= List.of(NAVER, KAKAO, DEVOCEAN, TOSS, MY_REAL_TRIP, LINE, DAANGN);

	public static Optional<Company> of(String value) {
		return Arrays.stream(Company.values())
			.filter(company -> company.getValue().equals(value))
			.findFirst();
	}

}
