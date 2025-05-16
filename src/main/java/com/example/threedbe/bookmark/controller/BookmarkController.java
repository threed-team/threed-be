package com.example.threedbe.bookmark.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.threedbe.bookmark.dto.request.BookmarkedPostRequest;
import com.example.threedbe.bookmark.dto.response.BookmarkedPostResponse;
import com.example.threedbe.bookmark.service.BookmarkService;
import com.example.threedbe.common.annotation.LoginMember;
import com.example.threedbe.common.dto.PageResponse;
import com.example.threedbe.member.domain.Member;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/bookmarks")
@RequiredArgsConstructor
public class BookmarkController implements BookmarkControllerSwagger {

	private final BookmarkService bookmarkService;

	@Override
	@PostMapping("/{postId}")
	public ResponseEntity<Void> createBookmark(@LoginMember Member member, @PathVariable("postId") Long postId) {
		bookmarkService.createBookmark(member, postId);

		return ResponseEntity.ok().build();
	}

	@Override
	@DeleteMapping("/{postId}")
	public ResponseEntity<Void> deleteBookmark(@LoginMember Member member, @PathVariable("postId") Long postId) {
		bookmarkService.deleteBookmark(member, postId);

		return ResponseEntity.ok().build();
	}

	@Override
	@GetMapping
	public ResponseEntity<PageResponse<BookmarkedPostResponse>> findBookmarkedPosts(
		@LoginMember Member member,
		@Valid BookmarkedPostRequest bookmarkedPostRequest) {

		PageResponse<BookmarkedPostResponse> posts = bookmarkService.findBookmarkedPosts(member, bookmarkedPostRequest);

		return ResponseEntity.ok(posts);
	}

}
