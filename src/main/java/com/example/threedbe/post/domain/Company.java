package com.example.threedbe.post.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Company {

	NAVER("네이버", "https://threed-uploaded-files.s3.ap-northeast-2.amazonaws.com/company-logo/naver.ico"),

	KAKAO("카카오", "https://threed-uploaded-files.s3.ap-northeast-2.amazonaws.com/company-logo/kakao.ico"),

	DEVOCEAN("데보션", "https://threed-uploaded-files.s3.ap-northeast-2.amazonaws.com/company-logo/devocean.ico"),

	TOSS("토스", "https://threed-uploaded-files.s3.ap-northeast-2.amazonaws.com/company-logo/toss.ico"),

	MY_REAL_TRIP("마이리얼트립", "https://threed-uploaded-files.s3.ap-northeast-2.amazonaws.com/company-logo/myrealtrip.ico"),

	LINE("라인", "https://threed-uploaded-files.s3.ap-northeast-2.amazonaws.com/company-logo/line.ico"),

	DAANGN("당근마켓", "https://threed-uploaded-files.s3.ap-northeast-2.amazonaws.com/company-logo/daangn.ico"),

	OLIVE_YOUNG("올리브영", "https://threed-uploaded-files.s3.ap-northeast-2.amazonaws.com/company-logo/oliveyoung.webp"),

	ETC("기타", null);

	private final String name;

	private final String logoImageUrl;

	public static final List<Company> MAIN_COMPANIES =
		List.of(NAVER, KAKAO, DEVOCEAN, TOSS, MY_REAL_TRIP, LINE, DAANGN);

	public static Optional<Company> of(String name) {
		return Arrays.stream(Company.values())
			.filter(company -> company.getName().equals(name))
			.findFirst();
	}

}
