package com.example.threedbe.post.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.threedbe.common.annotation.CurrentMember;
import com.example.threedbe.common.annotation.LoginMember;
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
import com.example.threedbe.post.service.MemberPostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/member-posts")
@RequiredArgsConstructor
public class MemberPostController implements MemberPostControllerSwagger {

	private final MemberPostService memberPostService;

	@Override
	@PostMapping
	public ResponseEntity<MemberPostSaveResponse> saveDraft(@LoginMember Member member) {
		MemberPostSaveResponse memberPostSaveResponse = memberPostService.saveDraft(member);

		return ResponseEntity.ok(memberPostSaveResponse);
	}

	@Override
	@PostMapping("/{postId}/images")
	public ResponseEntity<PresignedUrlResponse> generateImageUrl(
		@LoginMember Member member,
		@PathVariable("postId") Long postId,
		@RequestBody @Valid MemberPostImageRequest memberPostImageRequest) {

		PresignedUrlResponse presignedUrlResponse =
			memberPostService.generateImageUrl(member, postId, memberPostImageRequest);

		return ResponseEntity.ok(presignedUrlResponse);
	}

	@Override
	@PostMapping("/{postId}")
	public ResponseEntity<MemberPostSaveResponse> save(
		@LoginMember Member member,
		@PathVariable("postId") Long postId,
		@RequestBody @Valid MemberPostSaveRequest memberPostSaveRequest) {

		MemberPostSaveResponse memberPostSaveResponse = memberPostService.save(member, postId, memberPostSaveRequest);

		return ResponseEntity.ok(memberPostSaveResponse);
	}

	@Override
	@GetMapping("/search")
	public ResponseEntity<PageResponse<MemberPostResponse>> search(
		@Valid MemberPostSearchRequest memberPostSearchRequest) {

		PageResponse<MemberPostResponse> search = memberPostService.search(memberPostSearchRequest);

		return ResponseEntity.ok(search);
	}

	@Override
	@GetMapping("/{postId}")
	public ResponseEntity<MemberPostDetailResponse> findMemberPostDetail(
		@CurrentMember Member member,
		@PathVariable("postId") Long postId) {

		MemberPostDetailResponse memberPostDetailResponse = memberPostService.findMemberPostDetail(member, postId);

		return ResponseEntity.ok(memberPostDetailResponse);
	}

	@Override
	@GetMapping("/{postId}/edit")
	public ResponseEntity<MemberPostEditResponse> findMemberPostForEdit(
		@LoginMember Member member,
		@PathVariable("postId") Long postId) {

		MemberPostEditResponse memberPostEditResponse = memberPostService.findMemberPostForEdit(member, postId);

		return ResponseEntity.ok(memberPostEditResponse);
	}

	@Override
	@GetMapping("/popular")
	public ResponseEntity<ListResponse<MemberPostResponse>> findPopularMemberPosts(
		@Valid MemberPostPopularRequest memberPostPopularRequest) {

		ListResponse<MemberPostResponse> popularMemberPosts =
			memberPostService.findPopularMemberPosts(memberPostPopularRequest);

		return ResponseEntity.ok(popularMemberPosts);
	}

	@Override
	@PatchMapping("/{postId}")
	public ResponseEntity<MemberPostUpdateResponse> update(
		@LoginMember Member member,
		@PathVariable("postId") Long postId,
		@RequestBody @Valid MemberPostUpdateRequest memberPostUpdateRequest) {

		MemberPostUpdateResponse memberPostUpdateResponse =
			memberPostService.update(member, postId, memberPostUpdateRequest);

		return ResponseEntity.ok(memberPostUpdateResponse);
	}

	@Override
	@DeleteMapping("/{postId}")
	public ResponseEntity<Void> delete(@LoginMember Member member, @PathVariable("postId") Long postId) {
		memberPostService.delete(member, postId);

		return ResponseEntity.noContent().build();
	}

}
