package com.example.threedbe.bookmark.dto.request;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

@ParameterObject
public record BookmarkPageRequest(

	@Schema(description = "페이지 번호", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	@Positive
	int page,

	@Schema(description = "페이지 크기", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
	@Positive
	int size

) {
}
