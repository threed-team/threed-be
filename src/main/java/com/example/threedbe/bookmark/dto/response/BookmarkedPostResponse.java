package com.example.threedbe.bookmark.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.example.threedbe.post.domain.CompanyPost;
import com.example.threedbe.post.domain.MemberPost;
import com.example.threedbe.post.dto.response.AuthorResponse;

import io.swagger.v3.oas.annotations.media.Schema;

public record BookmarkedPostResponse(

	@Schema(description = "포스트 아이디", example = "2")
	long id,

	@Schema(description = "제목", example = "FE News 25년 5월 소식을 전해드립니다!")
	String title,

	@Schema(description = "썸네일 이미지 주소", example = "https://d2.naver.com/content/images/2023/07/-----------2023-07-06------4-16-49.png")
	String thumbnailImageUrl,

	@Schema(description = "분야", example = "Frontend")
	String field,

	@Schema(description = "조회수", example = "0")
	int viewCount,

	@Schema(description = "저자")
	AuthorResponse author,

	@Schema(description = "기술들", example = "[\"REACT\", \"JAVASCRIPT\"]")
	List<String> skills,

	@Schema(description = "생성일", example = "2025-05-08T20:12:14")
	LocalDateTime createdAt,

	@Schema(description = "회사 포스트 여부", example = "true")
	boolean isCompany,

	@Schema(description = "신규 포스트 여부", example = "true")
	boolean isNew,

	@Schema(description = "인기 포스트 여부", example = "true")
	boolean isHot

) {

	public static BookmarkedPostResponse from(CompanyPost companyPost, boolean isNew, boolean isHot) {

		return new BookmarkedPostResponse(
			companyPost.getId(),
			companyPost.getTitle(),
			companyPost.getThumbnailImageUrl(),
			companyPost.getField().getName(),
			companyPost.getViewCount(),
			AuthorResponse.from(companyPost.getCompany()),
			null,
			companyPost.getPublishedAt(),
			true,
			isNew,
			isHot
		);
	}

	public static BookmarkedPostResponse from(MemberPost memberPost, boolean isNew, boolean isHot) {

		return new BookmarkedPostResponse(
			memberPost.getId(),
			memberPost.getTitle(),
			memberPost.getThumbnailImageUrl(),
			memberPost.getField().getName(),
			memberPost.getViewCount(),
			AuthorResponse.from(memberPost.getMember()),
			memberPost.getSkills()
				.stream()
				.map(skill -> skill.getSkill().getName())
				.toList(),
			memberPost.getPublishedAt(),
			false,
			isNew,
			isHot
		);
	}

}
