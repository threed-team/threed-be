package com.example.threedbe.post.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

public record MemberPostSaveRequest(

	@NotBlank
	String title,

	@NotBlank
	String content,

	@NotBlank
	String field,

	List<String> skills

) {
}
