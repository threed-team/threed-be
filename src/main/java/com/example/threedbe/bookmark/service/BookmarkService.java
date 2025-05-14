package com.example.threedbe.bookmark.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.threedbe.bookmark.domain.Bookmark;
import com.example.threedbe.bookmark.repository.BookmarkRepository;
import com.example.threedbe.common.exception.ThreedConflictException;
import com.example.threedbe.common.exception.ThreedNotFoundException;
import com.example.threedbe.member.domain.Member;
import com.example.threedbe.post.domain.Post;
import com.example.threedbe.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

	private final BookmarkRepository bookmarkRepository;
	private final PostRepository postRepository;

	@Transactional
	public void createBookmark(Member member, Long postId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new ThreedNotFoundException("존재하지 않는 포스트입니다."));

		boolean isAlreadyBookmarked = bookmarkRepository.existsByMemberAndPost(member, post);
		if (isAlreadyBookmarked) {
			throw new ThreedConflictException("이미 북마크한 포스트입니다.");
		}

		member.addBookmark(post);
	}

	@Transactional
	public void deleteBookmark(Member member, Long postId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new ThreedNotFoundException("존재하지 않는 포스트입니다."));

		// TODO: LIMIT 1 걸기
		Bookmark bookmark = bookmarkRepository.findByMemberAndPost(member, post)
			.orElseThrow(() -> new ThreedNotFoundException("북마크하지 않은 포스트입니다."));

		member.removeBookmark(bookmark);
	}

}
