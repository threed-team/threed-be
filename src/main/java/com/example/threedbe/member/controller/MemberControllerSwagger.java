package com.example.threedbe.member.controller;

import org.springframework.http.ResponseEntity;

import com.example.threedbe.common.annotation.SwaggerErrorCode400;
import com.example.threedbe.common.annotation.SwaggerErrorCode401;
import com.example.threedbe.common.annotation.SwaggerErrorCode404;
import com.example.threedbe.common.annotation.SwaggerErrorCode500;
import com.example.threedbe.common.dto.PageResponse;
import com.example.threedbe.member.domain.Member;
import com.example.threedbe.member.dto.request.AuthoredPostRequest;
import com.example.threedbe.member.dto.response.AuthoredPostResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Member API")
@SecurityRequirement(name = "Authorization")
public interface MemberControllerSwagger {

	@Operation(
		summary = "작성한 포스트 리스트 조회",
		description = "page, size 없이 요청하면 기본값으로 page=1, size=20이 적용됩니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "작성한 포스트 리스트 조회 성공")
		})
	@SwaggerErrorCode400
	@SwaggerErrorCode401
	@SwaggerErrorCode404(description = "존재하지 않는 회원인 경우")
	@SwaggerErrorCode500
	ResponseEntity<PageResponse<AuthoredPostResponse>> findAuthoredPosts(
		@Parameter(hidden = true) Member member,
		AuthoredPostRequest authoredPostRequest);

}
