package com.example.threedbe.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthProvider {

	@Enumerated(EnumType.STRING)
	@Column(name = "provider_type", nullable = false)
	private ProviderType providerType;

	@Column(name = "provider_id", nullable = false)
	private String providerId;

	// ✅ 전체 생성자
	public AuthProvider(ProviderType providerType, String providerId) {
		this.providerType = providerType;
		this.providerId = providerId;
	}

	// ⚠️ 이 생성자는 사용 안 해도 됩니다. 그냥 위에 하나만 있어도 충분해요.
}
