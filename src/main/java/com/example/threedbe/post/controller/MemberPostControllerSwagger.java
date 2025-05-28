package com.example.threedbe.post.controller;

import org.springframework.http.ResponseEntity;

import com.example.threedbe.common.annotation.SwaggerErrorCode400;
import com.example.threedbe.common.annotation.SwaggerErrorCode401;
import com.example.threedbe.common.annotation.SwaggerErrorCode404;
import com.example.threedbe.common.annotation.SwaggerErrorCode500;
import com.example.threedbe.common.dto.ListResponse;
import com.example.threedbe.common.dto.PageResponse;
import com.example.threedbe.member.domain.Member;
import com.example.threedbe.post.dto.request.MemberPostImageRequest;
import com.example.threedbe.post.dto.request.MemberPostPopularRequest;
import com.example.threedbe.post.dto.request.MemberPostSaveRequest;
import com.example.threedbe.post.dto.request.MemberPostSearchRequest;
import com.example.threedbe.post.dto.request.MemberPostUpdateRequest;
import com.example.threedbe.post.dto.response.MemberPostDetailResponse;
import com.example.threedbe.post.dto.response.MemberPostEditResponse;
import com.example.threedbe.post.dto.response.MemberPostResponse;
import com.example.threedbe.post.dto.response.MemberPostSaveResponse;
import com.example.threedbe.post.dto.response.MemberPostUpdateResponse;
import com.example.threedbe.post.dto.response.PresignedUrlResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MemberPost API")
public interface MemberPostControllerSwagger {

	@Operation(
		summary = "회원 포스트 임시 생성",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "회원 포스트 임시 생성 성공",
				content = @Content(schema = @Schema(implementation = MemberPostSaveResponse.class)))
		})
	@SwaggerErrorCode401
	@SwaggerErrorCode500
	@SecurityRequirement(name = "Authorization")
	ResponseEntity<MemberPostSaveResponse> saveDraft(@Parameter(hidden = true) Member member);

	@Operation(
		summary = "회원 포스트 이미지 URL 생성",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "회원 포스트 이미지 URL 생성 성공",
				content = @Content(schema = @Schema(implementation = PresignedUrlResponse.class)))
		})
	@SwaggerErrorCode400
	@SwaggerErrorCode401
	@SwaggerErrorCode404(description = "회원 포스트가 존재하지 않는 경우")
	@SwaggerErrorCode500
	@SecurityRequirement(name = "Authorization")
	ResponseEntity<PresignedUrlResponse> generateImageUrl(
		@Parameter(hidden = true) Member member,
		Long postId,
		MemberPostImageRequest memberPostImageRequest);

	@Operation(
		summary = "회원 포스트 생성(릴리즈)",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "회원 포스트 생성(릴리즈) 성공",
				content = @Content(schema = @Schema(implementation = MemberPostSaveResponse.class)))
		})
	@SwaggerErrorCode400
	@SwaggerErrorCode401
	@SwaggerErrorCode404(description = "회원 포스트가 존재하지 않는 경우")
	@SwaggerErrorCode500
	@SecurityRequirement(name = "Authorization")
	ResponseEntity<MemberPostSaveResponse> save(
		@Parameter(hidden = true) Member member,
		Long postId,
		MemberPostSaveRequest memberPostSaveRequest);

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

	@Operation(
		summary = "회원 포스트 상세 조회",
		description = "인증은 선택적입니다. 인증된 사용자의 경우 북마크 여부(isBookmarked), 내 포스트 여부(isMyPost)가 추가 제공",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "회원 포스트 상세 조회 성공",
				content = @Content(schema = @Schema(implementation = MemberPostDetailResponse.class))),
		})
	@SwaggerErrorCode400
	@SwaggerErrorCode404(description = "회원 포스트가 존재하지 않는 경우")
	@SwaggerErrorCode500
	@SecurityRequirement(name = "Authorization")
	ResponseEntity<MemberPostDetailResponse> findMemberPostDetail(@Parameter(hidden = true) Member member, Long postId);

	@Operation(
		summary = "회원 포스트 수정용 상세 조회",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "회원 포스트 수정용 상세 조회 성공",
				content = @Content(schema = @Schema(implementation = MemberPostEditResponse.class))),
		})
	@SwaggerErrorCode400
	@SwaggerErrorCode401
	@SwaggerErrorCode404(description = "회원 포스트가 존재하지 않는 경우")
	@SwaggerErrorCode500
	@SecurityRequirement(name = "Authorization")
	ResponseEntity<MemberPostEditResponse> findMemberPostForEdit(@Parameter(hidden = true) Member member, Long postId);

	@Operation(
		summary = "인기 회원 포스트 리스트 조회",
		description = "인기 조건은 WEEK, MONTH 중 하나로 설정 가능하며, 기본값은 WEEK입니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "인기 회원 포스트 리스트 조회 성공")
		})
	@SwaggerErrorCode400
	@SwaggerErrorCode500
	ResponseEntity<ListResponse<MemberPostResponse>> findPopularMemberPosts(
		MemberPostPopularRequest memberPostPopularRequest);

	@Operation(
		summary = "회원 포스트 수정",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "회원 포스트 수정 성공",
				content = @Content(schema = @Schema(implementation = MemberPostSaveResponse.class)))
		})
	@SwaggerErrorCode400
	@SwaggerErrorCode401
	@SwaggerErrorCode404(description = "회원 포스트가 존재하지 않는 경우")
	@SwaggerErrorCode500
	@SecurityRequirement(name = "Authorization")
	ResponseEntity<MemberPostUpdateResponse> update(
		@Parameter(hidden = true) Member member,
		Long postId,
		MemberPostUpdateRequest memberPostUpdateRequest);

	@Operation(
		summary = "회원 포스트 삭제",
		responses = {
			@ApiResponse(
				responseCode = "204",
				description = "회원 포스트 삭제 성공")
		})
	@SwaggerErrorCode400
	@SwaggerErrorCode401
	@SwaggerErrorCode404(description = "회원 포스트가 존재하지 않는 경우")
	@SwaggerErrorCode500
	@SecurityRequirement(name = "Authorization")
	ResponseEntity<Void> delete(@Parameter(hidden = true) Member member, Long postId);

}
