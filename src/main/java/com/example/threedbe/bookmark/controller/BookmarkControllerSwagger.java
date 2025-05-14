package com.example.threedbe.bookmark.controller;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import com.example.threedbe.common.annotation.SwaggerErrorCode400;
import com.example.threedbe.common.annotation.SwaggerErrorCode401;
import com.example.threedbe.common.annotation.SwaggerErrorCode404;
import com.example.threedbe.common.annotation.SwaggerErrorCode500;
import com.example.threedbe.member.domain.Member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Bookmark API")
@SecurityRequirement(name = "Authorization")
public interface BookmarkControllerSwagger {

	@Operation(
		summary = "북마크 생성",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "북마크 생성 성공"),
			@ApiResponse(
				responseCode = "409",
				description = "이미 북마크한 포스트인 경우",
				content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
		})
	@SwaggerErrorCode400
	@SwaggerErrorCode401
	@SwaggerErrorCode404(description = "존재하지 않는 포스트인 경우")
	@SwaggerErrorCode500
	ResponseEntity<Void> createBookmark(@Parameter(hidden = true) Member member, Long postId);

	@Operation(
		summary = "북마크 삭제",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "북마크 삭제 성공")
		})
	@SwaggerErrorCode400
	@SwaggerErrorCode401
	@SwaggerErrorCode404(description = "존재하지 않는 포스트인 경우, 존재하지 않는 북마크인 경우")
	@SwaggerErrorCode500
	ResponseEntity<Void> deleteBookmark(@Parameter(hidden = true) Member member, Long postId);

}
