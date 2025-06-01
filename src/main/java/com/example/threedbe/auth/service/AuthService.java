package com.example.threedbe.auth.service;

import org.springframework.stereotype.Service;

import com.example.threedbe.auth.domain.AccessToken;
import com.example.threedbe.auth.domain.RefreshToken;
import com.example.threedbe.member.domain.Member;
import com.example.threedbe.member.repository.MemberRepository;
import com.example.threedbe.member.service.MemberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final JwtTokenProvider jwtTokenProvider;
	private final MemberService memberService;
	private final MemberRepository memberRepository;

	public Member parseAccessToken(String rawAccessToken) {
		AccessToken accessToken = new AccessToken(rawAccessToken);
		jwtTokenProvider.validate(accessToken);
		long memberId = jwtTokenProvider.parseAccessToken(accessToken);
		return memberService.findById(memberId);
	}

	public String reissueAccessToken(String refreshTokenValue) {
		RefreshToken refreshToken = new RefreshToken(refreshTokenValue);
		jwtTokenProvider.validate(refreshToken);
		long memberId = jwtTokenProvider.parseRefreshToken(refreshToken);
		Member member = memberService.findById(memberId);
		return jwtTokenProvider.createAccessToken(member.getId()).getValue();
	}

	public void logout(Member member, HttpServletResponse response) {
		member.deleteRefreshToken();
		memberRepository.save(member);

		Cookie accessTokenCookie = new Cookie("accessToken", null);
		accessTokenCookie.setHttpOnly(true);
		accessTokenCookie.setPath("/");
		accessTokenCookie.setMaxAge(0);

		Cookie refreshTokenCookie = new Cookie("refreshToken", null);
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setPath("/");
		refreshTokenCookie.setMaxAge(0);

		response.addCookie(accessTokenCookie);
		response.addCookie(refreshTokenCookie);
	}
}
