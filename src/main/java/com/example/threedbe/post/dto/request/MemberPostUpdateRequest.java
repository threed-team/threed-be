package com.example.threedbe.post.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record MemberPostUpdateRequest(

	@Schema(description = "제목", example = "구합니다")
	@NotBlank
	String title,

	@Schema(description = "내용", example = "개발자 구합니다")
	@NotBlank
	String content,

	@Schema(description = "분야", example = "Frontend")
	@NotBlank
	String field,

	@Schema(description = "기술들", example = "[\"JAVASCRIPT\", \"REACT\"]")
	List<String> skills

) {
}
