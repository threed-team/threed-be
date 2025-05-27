package com.example.threedbe.post.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.threedbe.post.domain.CompanyPost;

public interface CompanyPostRepository extends JpaRepository<CompanyPost, Long>, CompanyPostRepositoryCustom {

	Optional<Long> findNextId(LocalDateTime publishedAt);

	Optional<Long> findPreviousId(LocalDateTime publishedAt);

	@Query("SELECT cp.id FROM CompanyPost cp WHERE (cp.publishedAt < :publishedAt) ORDER BY cp.publishedAt DESC LIMIT 1")
	Optional<Long> findNextId(@Param("publishedAt") LocalDateTime publishedAt);

	@Query("SELECT cp.id FROM CompanyPost cp WHERE (cp.publishedAt > :publishedAt) ORDER BY cp.publishedAt ASC LIMIT 1")
	Optional<Long> findPrevId(@Param("publishedAt") LocalDateTime publishedAt);

	@Query("SELECT cp FROM CompanyPost cp WHERE cp.publishedAt > :publishedAt " +
		"ORDER BY (cp.viewCount + SIZE(cp.bookmarks) * 2) DESC LIMIT 10")
	List<CompanyPost> findCompanyPostsOrderByPopularity(@Param("publishedAt") LocalDateTime publishedAt);

}
