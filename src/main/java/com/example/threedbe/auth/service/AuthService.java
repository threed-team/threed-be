package com.example.threedbe.auth.service;

import org.springframework.stereotype.Service;

import com.example.threedbe.auth.config.GoogleOAuthProperties;
import com.example.threedbe.auth.domain.AccessToken;
import com.example.threedbe.auth.domain.RefreshToken;
import com.example.threedbe.auth.service.client.GoogleOAuthClient;
import com.example.threedbe.auth.service.client.GoogleUserInfo;
import com.example.threedbe.member.domain.AuthProvider;
import com.example.threedbe.member.domain.Member;
import com.example.threedbe.member.domain.ProviderType;
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
	private final GoogleOAuthClient googleOAuthClient;
	private final GoogleOAuthProperties googleOAuthProperties;
	private final MemberRepository memberRepository;

	//  Access Token 파싱 → 인증된 사용자 조회
	public Member parseAccessToken(String rawAccessToken) {
		AccessToken accessToken = new AccessToken(rawAccessToken);
		jwtTokenProvider.validate(accessToken);
		long memberId = jwtTokenProvider.parseAccessToken(accessToken);
		return memberService.findById(memberId);
	}

	//  소셜 로그인 흐름 처리
	public String loginWithGoogle(String code, HttpServletResponse response) {
		// 1. code → access_token 요청
		String accessTokenFromGoogle = googleOAuthClient.requestAccessToken(
			code,
			googleOAuthProperties.getClientId(),
			googleOAuthProperties.getClientSecret(),
			googleOAuthProperties.getRedirectUri()
		);

		// 2. access_token → 사용자 정보 요청
		GoogleUserInfo userInfo = googleOAuthClient.requestUserInfo(accessTokenFromGoogle);

		// 3. 이메일 기준으로 기존 회원 조회 or 회원가입
		Member member = memberRepository.findByEmail(userInfo.email())
			.orElseGet(() -> {
				AuthProvider authProvider = new AuthProvider(ProviderType.GOOGLE, userInfo.id());
				Member newMember = new Member(
					authProvider,
					userInfo.email(),
					userInfo.name(),
					userInfo.picture()
				);
				return memberRepository.save(newMember);
			});

		// 4. JWT 발급
		AccessToken accessToken = jwtTokenProvider.createAccessToken(member.getId());
		RefreshToken refreshToken = jwtTokenProvider.createRefreshToken();

		// 5-1. Refresh Token을 DB에 저장
		member.updateRefreshToken(refreshToken);
		memberRepository.save(member);

		// 5-2. Refresh Token을 HttpOnly 쿠키에 저장
		Cookie refreshCookie = new Cookie("refreshToken", refreshToken.getValue());
		refreshCookie.setHttpOnly(true);
		refreshCookie.setPath("/");
		refreshCookie.setMaxAge(60 * 60 * 24 * 28);
		response.addCookie(refreshCookie);

		//  6. Access Token 쿠키 저장
		Cookie accessCookie = new Cookie("accessToken", accessToken.getValue());
		accessCookie.setHttpOnly(true);
		accessCookie.setPath("/");
		accessCookie.setMaxAge(60 * 60 * 2); // 2시간

		//  7. 쿠키들을 응답에 추가
		response.addCookie(refreshCookie);
		response.addCookie(accessCookie);

		//  8. 응답 바디는 메시지로 대체하거나 비워도 됩니다
		return "로그인 성공...";
	}

	//  refresh token을 통해 access token 재발급
	public String reissueAccessToken(String refreshTokenValue) {
		RefreshToken refreshToken = new RefreshToken(refreshTokenValue);
		jwtTokenProvider.validate(refreshToken);
		long memberId = jwtTokenProvider.parseRefreshToken(refreshToken);
		Member member = memberService.findById(memberId);

		return jwtTokenProvider.createAccessToken(member.getId()).getValue();
	}

	//  로그아웃
	public void logout(Member member, HttpServletResponse response) {
		// 1. DB에서 Refresh Token 삭제
		member.deleteRefreshToken();
		memberRepository.save(member);

		// 2. 쿠키 삭제
		Cookie accesstokenCookie = new Cookie("accessToken", null);
		accesstokenCookie.setHttpOnly(true);
		accesstokenCookie.setPath("/");
		accesstokenCookie.setMaxAge(0); // 쿠키 삭제

		Cookie refreshTokenCookie = new Cookie("refreshToken", null);
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setPath("/");
		refreshTokenCookie.setMaxAge(0); // 쿠키 삭제

		response.addCookie(accesstokenCookie);
		response.addCookie(refreshTokenCookie);
	}
}
