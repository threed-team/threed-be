package com.example.threedbe.post.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.threedbe.bookmark.dto.response.BookmarkedPostResponse;
import com.example.threedbe.post.domain.Post;

public interface PostRepositoryCustom {

	Optional<Post> findPostById(Long postId);

	Page<BookmarkedPostResponse> findBookmarkedPostsByMemberId(Long memberId, Pageable pageable);

}
