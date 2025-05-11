package com.example.threedbe.common.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import io.swagger.v3.oas.annotations.media.Schema;

public record PageResponse<T>(

	@Schema(description = "페이지에 포함된 데이터")
	List<T> elements,

	@Schema(description = "현재 페이지 번호", example = "1")
	int pageNumber,

	@Schema(description = "페이지 크기", example = "10")
	int pageSize,

	@Schema(description = "전체 데이터 개수", example = "100")
	long totalCount,

	@Schema(description = "전체 페이지 개수", example = "10")
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

