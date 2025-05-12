package com.example.threedbe.post.dto.request;

import static io.swagger.v3.oas.annotations.media.Schema.*;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Positive;

public record CompanyPostSearchRequest(

	@Schema(description = "분야 이름 목록", example = "[\"Frontend\", \"Backend\", \"DevOps\"]")
	@Nullable
	List<String> fields,

	@Schema(description = "회사 이름 목록", example = "[\"네이버\", \"카카오\", \"라인\"]")
	@Nullable
	List<String> companies,

	@Schema(description = "페이지 번호", example = "1", requiredMode = RequiredMode.REQUIRED)
	@Positive
	int page,

	@Schema(description = "페이지 크기", example = "10", requiredMode = RequiredMode.REQUIRED)
	@Positive
	int size,

	@Schema(description = "검색 키워드", example = "개발자")
	@Nullable
	String keyword

) {
}
