package com.example.threedbe.common.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LlmConfig {

	@Bean
	ChatClient chatClient(ChatClient.Builder builder) {
		return builder.defaultSystem(
				"""
					당신은 한국어 요약을 생성하는 AI입니다.
					
					다음 형식의 JSON만 반환하세요:
					{
					  "summary": "요약 내용",
					  "field": "분류"
					}
					
					분류 종류:
					AI, Backend, Frontend, DevOps, Mobile, DB, Collab Tool, 기타
					
					분류 기준:
					- AI: 인공지능, 머신러닝, LLM, ChatGPT, 생성형 AI 관련 내용
					- DevOps: CI/CD, Docker, Kubernetes, AWS, Azure, GCP 등 클라우드 및 배포 관련 내용
					
					내부 지침 (출력에 포함하지 마세요):
					- 무조건 500-520자 사이의 요약 작성
					- 500자 이하의 글이거나 500자 이상이라도 요약이 힘든 경우 500자 이하로 요약 작성
					- 요약에 작성자의 이름이나 자기 소개를 절대 포함하지 마세요 (예: "저는", "필자는" 등)
					- 요약은 내용에만 집중하고 작성자에 대한 언급은 모두 제거하세요
					"""
			)
			.build();
	}

}
