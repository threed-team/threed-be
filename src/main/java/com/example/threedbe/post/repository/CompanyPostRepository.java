package com.example.threedbe.post.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.threedbe.post.domain.CompanyPost;

public interface CompanyPostRepository extends JpaRepository<CompanyPost, Long>, CompanyPostRepositoryCustom {

	Optional<Long> findNextId(LocalDateTime publishedAt);

	Optional<Long> findPreviousId(LocalDateTime publishedAt);

	List<CompanyPost> findPopularPosts(LocalDateTime publishedAfter);

}
