package com.example.threedbe.post.controller;

import org.springframework.http.ResponseEntity;

import com.example.threedbe.common.annotation.SwaggerErrorCode400;
import com.example.threedbe.common.annotation.SwaggerErrorCode404;
import com.example.threedbe.common.annotation.SwaggerErrorCode500;
import com.example.threedbe.common.dto.PageResponse;
import com.example.threedbe.member.domain.Member;
import com.example.threedbe.post.dto.request.CompanyPostSearchRequest;
import com.example.threedbe.post.dto.response.CompanyPostDetailResponse;
import com.example.threedbe.post.dto.response.CompanyPostResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "CompanyPost API")
public interface CompanyPostControllerSwagger {

	@Operation(
		summary = "회사 포스트 검색",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "회사 포스트 조회 성공")
		})
	@SwaggerErrorCode400
	@SwaggerErrorCode404(description = "등록된 회사, 분야가 아닌 경우")
	@SwaggerErrorCode500
	ResponseEntity<PageResponse<CompanyPostResponse>> search(CompanyPostSearchRequest companyPostSearchRequest);

	@Operation(
		summary = "회사 포스트 상세 조회",
		description = "인증은 선택적입니다. 인증된 사용자의 경우 북마크 여부(isBookmarked)가 추가 제공",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "회사 포스트 상세 조회 성공",
				content = @Content(schema = @Schema(implementation = CompanyPostDetailResponse.class))),
		})
	@SwaggerErrorCode400
	@SwaggerErrorCode404(description = "회사 포스트가 존재하지 않는 경우")
	@SwaggerErrorCode500
	@SecurityRequirement(name = "Authorization")
	ResponseEntity<CompanyPostDetailResponse> getCompanyPostDetail(
		@Parameter(hidden = true) Member member,
		Long postId);

}
