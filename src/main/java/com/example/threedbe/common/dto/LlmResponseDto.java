package com.example.threedbe.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * LLM 응답 DTO
 * LLM이 생성한 요약 및 분야 정보를 담는 DTO
 */
public record LlmResponseDto(
	@JsonProperty(required = true, value = "summary") String summary,
	@JsonProperty(required = true, value = "field") String field
) {
}
