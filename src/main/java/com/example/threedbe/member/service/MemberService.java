package com.example.threedbe.member.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.threedbe.common.dto.PageResponse;
import com.example.threedbe.common.exception.ThreedConflictException;
import com.example.threedbe.common.exception.ThreedNotFoundException;
import com.example.threedbe.common.exception.ThreedUnauthorizedException;
import com.example.threedbe.member.domain.AuthProvider;
import com.example.threedbe.member.domain.Member;
import com.example.threedbe.member.domain.ProviderType;
import com.example.threedbe.member.dto.request.AuthoredPostRequest;
import com.example.threedbe.member.dto.response.AuthoredPostResponse;
import com.example.threedbe.member.repository.MemberRepository;
import com.example.threedbe.post.domain.MemberPost;
import com.example.threedbe.post.domain.PopularCondition;
import com.example.threedbe.post.repository.MemberPostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

	private final MemberRepository memberRepository;
	private final MemberPostRepository memberPostRepository;

	public Member findById(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new ThreedNotFoundException("존재하지 않는 회원입니다."));

		if (member.isDeleted()) {
			throw new ThreedUnauthorizedException("탈퇴한 회원입니다.");
		}

		return member;
	}

	@Transactional
	public Member findOrCreate(
		String email,
		String name,
		String profileImageUrl,
		ProviderType providerType,
		String providerId
	) {
		//  providerId 기준으로 우선 조회하고, 이메일 중복되면 예외 발생
		return memberRepository.findByAuthProviderProviderTypeAndAuthProviderProviderId(providerType, providerId)
			.or(() -> {
				Optional<Member> sameEmail = memberRepository.findByEmail(email);
				if (sameEmail.isPresent()) {
					ProviderType existingProvider = sameEmail.get().getAuthProvider().getProviderType();
					throw new ThreedConflictException(String.format(
						"'%s' 이메일은 이미 %s 계정으로 가입되어 있습니다. 해당 소셜로 로그인해주세요.",
						email,
						existingProvider.name()
					));
				}
				AuthProvider authProvider = new AuthProvider(providerType, providerId);
				Member newMember = new Member(authProvider, email, name, profileImageUrl);
				return Optional.of(memberRepository.save(newMember));
			})
			.get();
	}

	//  providerType + providerId 기준으로 찾는 메서드
	public Optional<Member> findByProviderAndProviderId(ProviderType providerType, String providerId) {
		return memberRepository.findByAuthProviderProviderTypeAndAuthProviderProviderId(providerType, providerId);
	}

	//  이메일로 회원 찾는 메서드
	public Optional<Member> findByEmail(String email) {
		return memberRepository.findByEmail(email);
	}

	// TODO: N+1 문제 해결하기
	public PageResponse<AuthoredPostResponse> findAuthoredPosts(
		Member member,
		AuthoredPostRequest authoredPostRequest) {

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startDate = PopularCondition.WEEK.calculateStartDate(LocalDateTime.now());
		List<MemberPost> popularPosts = memberPostRepository.findMemberPostsOrderByPopularity(startDate);

		PageRequest pageRequest = PageRequest.of(authoredPostRequest.page() - 1, authoredPostRequest.size());
		Page<AuthoredPostResponse> authoredPosts = memberPostRepository.findByMemberIdOrderByCreatedAtDesc(
				member.getId(),
				pageRequest)
			.map(post -> {
				boolean isNew = post.getCreatedAt().isAfter(now.minusDays(7));
				boolean isHot = popularPosts.contains(post);
				return AuthoredPostResponse.from(post, isNew, isHot);
			});

		return PageResponse.from(authoredPosts);
	}
}
