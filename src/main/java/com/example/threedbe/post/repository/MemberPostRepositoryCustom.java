package com.example.threedbe.post.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.example.threedbe.post.domain.MemberPost;

public interface MemberPostRepositoryCustom {

	Optional<Long> findNextId(LocalDateTime publishedAt);

	Optional<Long> findPreviousId(LocalDateTime publishedAt);

	List<MemberPost> findPopularPosts(LocalDateTime publishedAfter);

	Optional<MemberPost> findMemberPostDetailById(Long postId);

}
