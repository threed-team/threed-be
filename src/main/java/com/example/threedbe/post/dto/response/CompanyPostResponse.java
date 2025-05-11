package com.example.threedbe.post.dto.response;

import java.time.LocalDateTime;

import com.example.threedbe.post.domain.CompanyPost;

public record CompanyPostResponse(

	Long id,

	String title,

	String content,

	String thumbnailImageUrl,

	String field,

	int viewCount,

	String company,

	LocalDateTime createdAt

) {

	public static CompanyPostResponse from(CompanyPost companyPost) {
		return new CompanyPostResponse(
			companyPost.getId(),
			companyPost.getTitle(),
			companyPost.getContent(),
			companyPost.getThumbnailImageUrl(),
			companyPost.getField().getValue(),
			companyPost.getViewCount(),
			companyPost.getCompany().getValue(),
			companyPost.getCreatedAt()
		);
	}

}
