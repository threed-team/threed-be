package com.example.threedbe.post.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.threedbe.common.annotation.CurrentMember;
import com.example.threedbe.common.dto.ListResponse;
import com.example.threedbe.common.dto.PageResponse;
import com.example.threedbe.member.domain.Member;
import com.example.threedbe.post.dto.request.MemberPostPopularRequest;
import com.example.threedbe.post.dto.request.MemberPostSearchRequest;
import com.example.threedbe.post.dto.response.MemberPostDetailResponse;
import com.example.threedbe.post.dto.response.MemberPostResponse;
import com.example.threedbe.post.service.MemberPostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/member-posts")
@RequiredArgsConstructor
public class MemberPostController implements MemberPostControllerSwagger {

	private final MemberPostService memberPostService;

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

	@GetMapping("/popular")
	public ResponseEntity<ListResponse<MemberPostResponse>> findPopularMemberPosts(
		@Valid MemberPostPopularRequest memberPostPopularRequest) {

		ListResponse<MemberPostResponse> popularMemberPosts =
			memberPostService.findPopularMemberPosts(memberPostPopularRequest);

		return ResponseEntity.ok(popularMemberPosts);
	}

}
