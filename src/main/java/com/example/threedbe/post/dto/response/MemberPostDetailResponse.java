package com.example.threedbe.post.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.example.threedbe.post.domain.MemberPost;

import io.swagger.v3.oas.annotations.media.Schema;

public record MemberPostDetailResponse(

	@Schema(description = "포스트 아이디", example = "1")
	long id,

	@Schema(description = "제목", example = "FE News 25년 5월 소식을 전해드립니다!")
	String title,

	@Schema(description = "내용", example = "2025년 5월의 소식에서는 리액트 컴파일러의 RC 단계 도달")
	String content,

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

	@Schema(description = "북마크수", example = "0")
	int bookmarkCount,

	@Schema(description = "북마크 여부", example = "true")
	boolean isBookmarked,

	@Schema(description = "내 포스트 여부", example = "true")
	boolean isMyPost,

	@Schema(description = "다음 포스트 아이디", example = "3")
	Long nextId,

	@Schema(description = "이전 포스트 아이디", example = "1")
	Long previousId

) {

	public static MemberPostDetailResponse from(
		MemberPost memberPost,
		int bookmarkCount,
		boolean isBookmarked,
		boolean isMyPost,
		Long nextId,
		Long previousId) {

		return new MemberPostDetailResponse(
			memberPost.getId(),
			memberPost.getTitle(),
			memberPost.getContent(),
			memberPost.getThumbnailImageUrl(),
			memberPost.getField().getName(),
			memberPost.getViewCount(),
			AuthorResponse.from(memberPost.getMember()),
			memberPost.getSkills()
				.stream()
				.map(skill -> skill.getSkill().getName())
				.toList(),
			memberPost.getPublishedAt(),
			bookmarkCount,
			isBookmarked,
			isMyPost,
			nextId,
			previousId
		);
	}

}
