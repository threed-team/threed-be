package com.example.threedbe.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.threedbe.common.exception.ThreedNotFoundException;
import com.example.threedbe.common.exception.ThreedUnauthorizedException;
import com.example.threedbe.member.domain.Member;
import com.example.threedbe.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

	private final MemberRepository memberRepository;

	public Member findById(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new ThreedNotFoundException("존재하지 않는 회원입니다."));

		if (member.isDeleted()) {
			throw new ThreedUnauthorizedException("탈퇴한 회원입니다.");
		}

		return member;
	}

}
