package com.example.threedbe.bookmark.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.example.threedbe.common.exception.ThreedBadRequestException;
import com.example.threedbe.post.domain.CompanyPost;
import com.example.threedbe.post.domain.MemberPost;
import com.example.threedbe.post.domain.Post;

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

	@Schema(description = "소속 회사", example = "네이버")
	String company,

	@Schema(description = "기술들", example = "[\"REACT\", \"JAVASCRIPT\"]")
	List<String> skills,

	@Schema(description = "생성일", example = "2025-05-08T20:12:14")
	LocalDateTime createdAt

) {

	public static BookmarkedPostResponse from(Post post) {
		if (post instanceof CompanyPost companyPost) {
			return from(companyPost);
		} else if (post instanceof MemberPost memberPost) {
			return from(memberPost);
		} else {
			throw new ThreedBadRequestException("지원하지 않는 게시글 타입입니다.");
		}
	}

	private static BookmarkedPostResponse from(CompanyPost companyPost) {
		return new BookmarkedPostResponse(
			companyPost.getId(),
			companyPost.getTitle(),
			companyPost.getThumbnailImageUrl(),
			companyPost.getField().getValue(),
			companyPost.getViewCount(),
			companyPost.getCompany().getValue(),
			null,
			companyPost.getCreatedAt()
		);
	}

	private static BookmarkedPostResponse from(MemberPost memberPost) {
		return new BookmarkedPostResponse(
			memberPost.getId(),
			memberPost.getTitle(),
			memberPost.getThumbnailImageUrl(),
			memberPost.getField().getValue(),
			memberPost.getViewCount(),
			null,
			memberPost.getSkills()
				.stream()
				.map(skill -> skill.getSkill().getName())
				.toList(),
			memberPost.getCreatedAt()
		);
	}

}
