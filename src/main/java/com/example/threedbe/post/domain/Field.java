package com.example.threedbe.post.domain;

import java.util.Arrays;
import java.util.Optional;

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

}
