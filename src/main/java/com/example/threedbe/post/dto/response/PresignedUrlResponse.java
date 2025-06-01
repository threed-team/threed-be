package com.example.threedbe.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record PresignedUrlResponse(

	@Schema(description = "Presigned URL로 S3에 PUT할 때 사용", example = "https://example.com/presigned-url")
	String presignedUrl,

	@Schema(description = "파일 URL로 조회할 때 사용", example = "https://cdn.example.com/file-url")
	String fileUrl

) {
}
