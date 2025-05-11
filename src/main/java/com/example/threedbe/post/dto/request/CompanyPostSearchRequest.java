package com.example.threedbe.post.dto.request;

import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Positive;

public record CompanyPostSearchRequest(

	@Nullable List<String> fields,

	@Nullable List<String> companies,

	@Positive int page,

	@Positive int size,

	@Nullable String keyword

) {
}
