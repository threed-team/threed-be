package com.example.threedbe.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.threedbe.member.domain.Member;
import com.example.threedbe.member.domain.ProviderType;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByEmail(String email);

	Optional<Member> findByAuthProviderProviderTypeAndAuthProviderProviderId(
		ProviderType providerType,
		String providerId
	);
}
