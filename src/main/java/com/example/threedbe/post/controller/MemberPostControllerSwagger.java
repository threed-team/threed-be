package com.example.threedbe.post.controller;

import org.springframework.http.ResponseEntity;

import com.example.threedbe.common.annotation.SwaggerErrorCode400;
import com.example.threedbe.common.annotation.SwaggerErrorCode404;
import com.example.threedbe.common.annotation.SwaggerErrorCode500;
import com.example.threedbe.common.dto.PageResponse;
import com.example.threedbe.post.dto.request.MemberPostSearchRequest;
import com.example.threedbe.post.dto.response.MemberPostResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MemberPost API")
public interface MemberPostControllerSwagger {

	@Operation(
		summary = "회원 포스트 검색",
		description = "기술과 분야는 모두 선택사항입니다. 검색어는 제목과 내용에서 검색됩니다. page, size 없이 요청하면 기본값으로 page=1, size=10이 적용됩니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "회원 포스트 검색 성공")
		})
	@SwaggerErrorCode400
	@SwaggerErrorCode404(description = "등록된 기술, 분야가 아닌 경우")
	@SwaggerErrorCode500
	ResponseEntity<PageResponse<MemberPostResponse>> search(MemberPostSearchRequest memberPostSearchRequest);

}
