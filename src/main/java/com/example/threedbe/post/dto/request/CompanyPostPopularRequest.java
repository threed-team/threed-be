package com.example.threedbe.post.dto.request;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@ParameterObject
public record CompanyPostPopularRequest(

	@Schema(description = "인기 조건", example = "MONTH", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull
	PopularCondition condition

) {
}
