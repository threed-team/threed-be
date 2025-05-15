package com.example.threedbe.common.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record ListResponse<T>(

	@Schema(description = "리스트 데이터")
	List<T> elements

) {

	public static <T> ListResponse<T> from(List<T> elements) {
		return new ListResponse<>(elements);
	}

}
