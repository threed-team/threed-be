package com.example.threedbe.post.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.example.threedbe.common.exception.ThreedNotFoundException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Field {

	AI("AI"),

	BACKEND("Backend"),

	FRONTEND("Frontend"),

	DEVOPS("DevOps"),

	DB("DB"),

	MOBILE("Mobile"),

	COLLAB_TOOL("Collab Tool"),

	ETC("기타");

	private final String name;

	public static Optional<Field> of(String name) {
		return Arrays.stream(Field.values())
			.filter(company -> company.getName().equals(name))
			.findFirst();
	}

	public static Field fromName(String name) {
		return of(name)
			.orElseThrow(() -> new ThreedNotFoundException("등록된 분야가 아닙니다: " + name));
	}

	public static List<Field> fromNames(List<String> names) {
		return Optional.ofNullable(names)
			.orElse(Collections.emptyList())
			.stream()
			.map(Field::fromName)
			.toList();
	}

}
