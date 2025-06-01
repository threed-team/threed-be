package com.example.threedbe.post.dto.request;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

@ParameterObject
public record MemberPostSearchRequest(

	@Schema(description = "분야 이름 목록", example = "[\"Frontend\", \"Backend\", \"DevOps\"]")
	List<String> fields,

	@Schema(description = "기술 이름 목록", example = "[\"JAVASCRIPT\", \"REACT\"]")
	List<String> skills,

	@Schema(description = "페이지 번호", example = "1")
	@Positive
	Integer page,

	@Schema(description = "페이지 크기", example = "10")
	@Positive
	Integer size,

	@Schema(description = "검색 키워드", example = "개발자")
	String keyword

) {

	public MemberPostSearchRequest {
		if (page == null) {
			page = 1;
		}

		if (size == null) {
			size = 10;
		}
	}

	public PageRequest toPageRequest() {
		return PageRequest.of(page - 1, size);
	}

	public String extractKeyword() {
		return StringUtils.hasText(keyword) ? keyword : null;
	}

}
