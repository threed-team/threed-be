package com.example.threedbe.post.dto.request;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@ParameterObject
public record CompanyPostPopularRequest(

	@Schema(description = "인기 조건 (WEEK, MONTH)", example = "WEEK", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank
	String condition

) {

	public CompanyPostPopularRequest {
		if (condition == null) {
			condition = "WEEK";
		}
	}

}
