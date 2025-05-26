package com.example.threedbe.post.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MemberPostImageRequest(

	@NotBlank
	String fileName

) {
}
