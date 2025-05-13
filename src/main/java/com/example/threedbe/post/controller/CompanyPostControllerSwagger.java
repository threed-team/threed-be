package com.example.threedbe.post.controller;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import com.example.threedbe.common.dto.PageResponse;
import com.example.threedbe.post.dto.request.CompanyPostSearchRequest;
import com.example.threedbe.post.dto.response.CompanyPostDetailResponse;
import com.example.threedbe.post.dto.response.CompanyPostResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "CompanyPost API")
public interface CompanyPostControllerSwagger {

	@Operation(
		summary = "회사 포스트 검색",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "회사 포스트 조회 성공"),
			@ApiResponse(
				responseCode = "400",
				description = "등록된 회사, 분야가 아닌 경우",
				content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
		})
	ResponseEntity<PageResponse<CompanyPostResponse>> search(CompanyPostSearchRequest companyPostSearchRequest);

	@Operation(
		summary = "회사 포스트 상세 조회",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "회사 포스트 상세 조회 성공"),
			@ApiResponse(
				responseCode = "404",
				description = "회사 포스트가 존재하지 않는 경우",
				content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
		})
	ResponseEntity<CompanyPostDetailResponse> getCompanyPostDetail(long id);

}
