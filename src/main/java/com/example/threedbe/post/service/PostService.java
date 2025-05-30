package com.example.threedbe.post.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.threedbe.common.exception.ThreedNotFoundException;
import com.example.threedbe.post.domain.Post;
import com.example.threedbe.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

	private final PostRepository postRepository;

	public Post findById(Long postId) {
		return postRepository.findPostById(postId)
			.orElseThrow(() -> new ThreedNotFoundException("존재하지 않는 포스트입니다."));
	}

}
