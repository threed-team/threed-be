package com.example.threedbe.post.dto.request;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

@ParameterObject
public record CompanyPostSearchRequest(

	@Schema(description = "분야 이름 목록", example = "[\"Frontend\", \"Backend\", \"DevOps\"]")
	List<String> fields,

	@Schema(description = "회사 이름 목록", example = "[\"네이버\", \"카카오\", \"라인\"]")
	List<String> companies,

	@Schema(description = "페이지 번호", example = "1")
	@Positive
	Integer page,

	@Schema(description = "페이지 크기", example = "10")
	@Positive
	Integer size,

	@Schema(description = "검색 키워드", example = "개발자")
	String keyword

) {

	public CompanyPostSearchRequest {
		if (page == null) {
			page = 1;
		}

		if (size == null) {
			size = 10;
		}
	}

}
