package com.example.threedbe.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.threedbe.auth.dto.TokenResponse;
import com.example.threedbe.auth.service.AuthService;
import com.example.threedbe.auth.service.OAuthLoginService;
import com.example.threedbe.member.domain.Member;
import com.example.threedbe.member.domain.ProviderType;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final OAuthLoginService oAuthLoginService;
	private final AuthService authService;

	@GetMapping("/google/callback")
	public ResponseEntity<TokenResponse> googleCallback(@RequestParam("code") String code,
		HttpServletResponse response) {
		TokenResponse tokenResponse = oAuthLoginService.login(ProviderType.GOOGLE, code, response);
		return ResponseEntity.ok(tokenResponse);
	}

	@GetMapping("/kakao/callback")
	public ResponseEntity<TokenResponse> kakaoCallback(@RequestParam("code") String code,
		HttpServletResponse response) {
		TokenResponse tokenResponse = oAuthLoginService.login(ProviderType.KAKAO, code, response);
		return ResponseEntity.ok(tokenResponse);
	}

	@GetMapping("/github/callback")
	public ResponseEntity<TokenResponse> githubCallback(@RequestParam("code") String code,
		HttpServletResponse response) {
		TokenResponse tokenResponse = oAuthLoginService.login(ProviderType.GITHUB, code, response);
		return ResponseEntity.ok(tokenResponse);
	}

	@GetMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
		String accessToken = extractCookie(request, "accessToken");
		if (accessToken != null) {
			Member member = authService.parseAccessToken(accessToken);
			authService.logout(member, response);
		} else {
			authService.logout(null, response); // 쿠키만 삭제
		}
		return ResponseEntity.ok().build();
	}

	private String extractCookie(HttpServletRequest request, String name) {
		if (request.getCookies() == null)
			return null;
		for (Cookie cookie : request.getCookies()) {
			if (cookie.getName().equals(name)) {
				return cookie.getValue();
			}
		}
		return null;
	}
}
