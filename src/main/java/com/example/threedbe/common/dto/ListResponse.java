package com.example.threedbe.common.dto;

import java.util.List;

public record ListResponse<T>(

	List<T> elements

) {

	public static <T> ListResponse<T> from(List<T> elements) {
		return new ListResponse<>(elements);
	}

}
