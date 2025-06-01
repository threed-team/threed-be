package com.example.threedbe.post.dto.response;

import com.example.threedbe.post.domain.MemberPost;

import io.swagger.v3.oas.annotations.media.Schema;

public record MemberPostSaveResponse(

	@Schema(description = "저장된 게시물 ID", example = "1")
	Long postId

) {

	public static MemberPostSaveResponse from(MemberPost memberPost) {

		return new MemberPostSaveResponse(memberPost.getId());
	}

}
