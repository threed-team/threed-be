package com.example.threedbe.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.threedbe.post.domain.Post;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

	// TODO: QueryDSL로 변경
	@Query("SELECT DISTINCT p FROM Post p JOIN p.bookmarks b " +
		"WHERE b.member.id = :memberId " +
		"AND (TYPE(p) <> MemberPost OR (TYPE(p) = MemberPost AND TREAT(p AS MemberPost).deletedAt IS NULL)) " +
		"ORDER BY p.publishedAt DESC")
	Page<Post> findByBookmarksMemberIdOrderByPublishedAtDesc(Long memberId, Pageable pageable);

}
