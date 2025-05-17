package com.example.threedbe.post.dto.request;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;

@ParameterObject
public record CompanyPostPopularRequest(

	@Schema(description = "인기 조건", example = "WEEK")
	String condition

) {

	public CompanyPostPopularRequest {
		if (condition == null) {
			condition = "WEEK";
		}
	}

}
