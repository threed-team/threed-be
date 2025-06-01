package com.example.threedbe.bookmark.dto.request;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

@ParameterObject
public record BookmarkedPostRequest(

	@Schema(description = "페이지 번호", example = "1")
	@Positive
	Integer page,

	@Schema(description = "페이지 크기", example = "20")
	@Positive
	Integer size

) {

	public BookmarkedPostRequest {
		if (page == null) {
			page = 1;
		}

		if (size == null) {
			size = 20;
		}
	}

	public PageRequest toPageRequest() {
		return PageRequest.of(page - 1, size);
	}

}
