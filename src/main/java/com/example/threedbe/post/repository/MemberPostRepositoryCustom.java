package com.example.threedbe.post.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.threedbe.post.domain.Field;
import com.example.threedbe.post.domain.MemberPost;

public interface MemberPostRepositoryCustom {

	Optional<MemberPost> findMemberPostById(Long postId);

	Page<MemberPost> searchMemberPosts(
		List<Field> fields,
		List<String> skillNames,
		String keyword,
		boolean excludeSkillNames,
		Pageable pageable);

	List<Long> findPopularPostIds(LocalDateTime publishedAfter);

	Optional<Long> findNextId(LocalDateTime publishedAt);

	Optional<Long> findPreviousId(LocalDateTime publishedAt);

	List<MemberPost> findPopularPosts(LocalDateTime publishedAfter);

	Optional<MemberPost> findMemberPostDetailById(Long postId);

	Page<MemberPost> findMemberPostsByMemberId(Long memberId, Pageable pageable);

}
