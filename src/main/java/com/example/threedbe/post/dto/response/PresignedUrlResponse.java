package com.example.threedbe.post.dto.response;

public record PresignedUrlResponse(

	String presignedUrl,

	String fileUrl

) {
}
