package com.example.threedbe.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthProvider {

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private ProviderType providerType;

	@Column(nullable = false)
	private String providerId;

}
