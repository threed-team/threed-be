package com.example.threedbe.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record MemberPostImageRequest(

	@Schema(description = "이미지 파일 이름", example = "example-image.jpg")
	@NotBlank
	String fileName

) {
}
