package com.example.threedbe.post.repository;

import java.util.Optional;

import com.example.threedbe.post.domain.Post;

public interface PostRepositoryCustom {

	Optional<Post> findPostById(Long postId);

}
