package com.example.threedbe.auth.service.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GoogleOAuthClient {

	private final ObjectMapper objectMapper = new ObjectMapper();

	//  STEP 1: 구글에 access_token 요청
	public String requestAccessToken(String code, String clientId, String clientSecret, String redirectUri) {
		String tokenUrl = "https://oauth2.googleapis.com/token";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		String body = UriComponentsBuilder.newInstance()
			.queryParam("code", code)
			.queryParam("client_id", clientId)
			.queryParam("client_secret", clientSecret)
			.queryParam("redirect_uri", redirectUri)
			.queryParam("grant_type", "authorization_code")
			.build().toUri()
			.getRawQuery();

		HttpEntity<String> request = new HttpEntity<>(body, headers);
		RestTemplate restTemplate = new RestTemplate();

		// 🔍 디버깅 로그 추가
		System.out.println("🟡 [requestAccessToken] 요청 시작");
		System.out.println("➡️ code: " + code);
		System.out.println("➡️ client_id: " + clientId);
		System.out.println("➡️ redirect_uri: " + redirectUri);
		System.out.println("📦 요청 body: " + body);

		ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, String.class);

		System.out.println(" access_token 응답: " + response.getBody());

		JsonNode json = parseJson(response.getBody());

		// null check 로그
		if (json.get("access_token") == null) {
			System.out.println("❌ access_token 필드가 없음!");
			throw new RuntimeException("access_token이 응답에 없습니다.");
		}

		return json.get("access_token").asText();
	}

	//  STEP 2: access_token으로 사용자 정보 요청
	public GoogleUserInfo requestUserInfo(String accessToken) {
		String url = "https://www.googleapis.com/oauth2/v2/userinfo";

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);

		HttpEntity<Void> request = new HttpEntity<>(headers);
		RestTemplate restTemplate = new RestTemplate();

		//  디버깅 로그
		System.out.println("🟢 [requestUserInfo] accessToken: " + accessToken);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

		System.out.println(" 사용자 정보 응답: " + response.getBody());

		JsonNode json = parseJson(response.getBody());

		// null check 로그
		if (json.get("email") == null || json.get("name") == null || json.get("picture") == null) {
			System.out.println("❌ 사용자 정보 필드 중 일부가 null입니다.");
			throw new RuntimeException("사용자 정보 응답이 올바르지 않습니다.");
		}

		return new GoogleUserInfo(
			json.get("id").asText(),
			json.get("email").asText(),
			json.get("name").asText(),
			json.get("picture").asText()
		);
	}

	private JsonNode parseJson(String json) {
		try {
			return objectMapper.readTree(json);
		} catch (Exception e) {
			System.out.println("❌ JSON 파싱 실패: " + json);
			throw new RuntimeException("JSON 파싱 실패", e);
		}
	}
}
