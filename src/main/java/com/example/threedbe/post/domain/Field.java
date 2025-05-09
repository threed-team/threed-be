package com.example.threedbe.post.domain;

public enum Field {

	AI("AI"),

	BACKEND("Backend"),

	FRONTEND("Frontend"),

	DEVOPS("DevOps"),

	DB("DB"),

	MOBILE("Mobile"),

	COLLAB_TOOL("Collab Tool"),

	ETC("기타");

	private final String value;

	Field(String value) {
		this.value = value;
	}

}
