package com.example.threedbe.post.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Company {

	NAVER("네이버", "https://cdn.threed.site/company-logo/naver.ico"),

	KAKAO("카카오", "https://cdn.threed.site/company-logo/kakao.ico"),

	DEVOCEAN("데보션", "https://cdn.threed.site/company-logo/devocean.ico"),

	TOSS("토스", "https://cdn.threed.site/company-logo/toss.ico"),

	MY_REAL_TRIP("마이리얼트립", "https://cdn.threed.site/company-logo/myrealtrip.ico"),

	LINE("라인", "https://cdn.threed.site/company-logo/line.ico"),

	DAANGN("당근마켓", "https://cdn.threed.site/company-logo/daangn.ico"),

	OLIVE_YOUNG("올리브영", "https://cdn.threed.site/company-logo/oliveyoung.webp"),

	ETC("기타", null);

	private final String name;

	private final String logoImageUrl;

	public static List<Company> filterExcludedCompanies(List<Company> companies) {
		List<Company> mainCompanies = List.of(NAVER, KAKAO, DEVOCEAN, TOSS, MY_REAL_TRIP, LINE, DAANGN);

		return mainCompanies
			.stream()
			.filter(company -> !companies.contains(company))
			.toList();
	}

	public static Optional<Company> of(String name) {
		return Arrays.stream(Company.values())
			.filter(company -> company.getName().equals(name))
			.findFirst();
	}

	public static Company fromName(String name) {
		return of(name)
			.orElseThrow(() -> new IllegalArgumentException("등록된 회사가 아닙니다: " + name));
	}

	public static List<Company> fromNames(List<String> names) {
		return Optional.ofNullable(names)
			.orElse(List.of())
			.stream()
			.map(Company::fromName)
			.toList();
	}

}
