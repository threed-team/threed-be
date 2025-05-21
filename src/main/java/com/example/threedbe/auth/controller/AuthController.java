package com.example.threedbe.auth.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.threedbe.auth.config.GoogleOAuthProperties;
import com.example.threedbe.auth.dto.TokenResponse;
import com.example.threedbe.auth.service.AuthService;
import com.example.threedbe.common.annotation.LoginMember;
import com.example.threedbe.member.domain.Member;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;
	private final GoogleOAuthProperties googleOAuthProperties;

	//  access token 재발급
	@GetMapping("/refresh-token")
	public ResponseEntity<TokenResponse> refreshAccessToken(
		@CookieValue("refreshToken") String refreshTokenValue
	) {
		String newAccessToken = authService.reissueAccessToken(refreshTokenValue);
		return ResponseEntity.ok(new TokenResponse(newAccessToken));
	}

	//  구글 로그인 페이지로 리디렉트
	@GetMapping("/google")
	public void redirectToGoogle(HttpServletResponse response) throws IOException {
		String redirectUrl = UriComponentsBuilder.fromUriString("https://accounts.google.com/o/oauth2/v2/auth")
			.queryParam("client_id", googleOAuthProperties.getClientId())
			.queryParam("redirect_uri", googleOAuthProperties.getRedirectUri())
			.queryParam("response_type", "code")
			.queryParam("scope",
				"https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile")
			.queryParam("access_type", "offline")
			.queryParam("prompt", "consent")
			.build()
			.toUriString();

		response.sendRedirect(redirectUrl);
	}

	//  구글 로그인 콜백
	@GetMapping("/google/callback")
	public ResponseEntity<?> googleCallback(@RequestParam("code") String code, HttpServletResponse response) {
		String accessToken = authService.loginWithGoogle(code, response);
		return ResponseEntity.ok().body(accessToken);
	}

	//  로그아웃
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@LoginMember Member member, HttpServletResponse response) {
		authService.logout(member, response);
		return ResponseEntity.ok().build();
	}
}
