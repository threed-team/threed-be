package com.example.threedbe.common.dto;

import java.util.List;

import org.springframework.data.domain.Page;

public record PageResponse<T>(

	List<T> elements,

	int pageNumber,

	int pageSize,

	long totalCount,

	int totalPage

) {

	public static <T> PageResponse<T> from(Page<T> page) {
		return new PageResponse<>(
			page.getContent(),
			page.getNumber() + 1,
			page.getSize(),
			page.getTotalElements(),
			page.getTotalPages()
		);
	}

}

