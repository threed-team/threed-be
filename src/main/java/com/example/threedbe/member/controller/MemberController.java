package com.example.threedbe.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.threedbe.common.annotation.LoginMember;
import com.example.threedbe.common.dto.PageResponse;
import com.example.threedbe.member.domain.Member;
import com.example.threedbe.member.dto.request.AuthoredPostRequest;
import com.example.threedbe.member.dto.response.AuthoredPostResponse;
import com.example.threedbe.member.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@GetMapping("/posts")
	public ResponseEntity<PageResponse<AuthoredPostResponse>> findAuthoredPosts(
		@LoginMember Member member,
		@Valid AuthoredPostRequest authoredPostRequest) {

		PageResponse<AuthoredPostResponse> posts = memberService.findAuthoredPosts(member, authoredPostRequest);

		return ResponseEntity.ok(posts);
	}

}
