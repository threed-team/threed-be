package com.example.threedbe.bookmark.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.threedbe.bookmark.service.BookmarkService;
import com.example.threedbe.common.annotation.LoginMember;
import com.example.threedbe.member.domain.Member;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

	private final BookmarkService bookmarkService;

	@PostMapping("/{postId}/bookmarks")
	public ResponseEntity<Void> createBookmark(@LoginMember Member member, @PathVariable Long postId) {
		bookmarkService.createBookmark(member, postId);

		return ResponseEntity.ok().build();
	}

}
