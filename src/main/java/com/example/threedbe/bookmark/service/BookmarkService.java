package com.example.threedbe.bookmark.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.threedbe.bookmark.domain.Bookmark;
import com.example.threedbe.common.exception.ThreedNotFoundException;
import com.example.threedbe.member.domain.Member;
import com.example.threedbe.post.domain.Post;
import com.example.threedbe.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

	private final PostRepository postRepository;

	@Transactional
	public void createBookmark(Member member, Long postId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new ThreedNotFoundException("존재하지 않는 포스트입니다."));

		Bookmark bookmark = new Bookmark(member, post);
		post.addBookmark(bookmark);
	}

}
