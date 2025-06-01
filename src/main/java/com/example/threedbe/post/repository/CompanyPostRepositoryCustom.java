package com.example.threedbe.post.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.threedbe.post.domain.Company;
import com.example.threedbe.post.domain.CompanyPost;
import com.example.threedbe.post.domain.Field;

public interface CompanyPostRepositoryCustom {
	Page<CompanyPost> searchCompanyPosts(
		List<Field> fields,
		List<Company> companies,
		String keyword,
		boolean excludeCompanies,
		Pageable pageable);

	List<Long> findPopularPostIds(LocalDateTime publishedAfter);

	Optional<Long> findNextId(LocalDateTime publishedAt);

	Optional<Long> findPreviousId(LocalDateTime publishedAt);

	List<CompanyPost> findPopularPosts(LocalDateTime publishedAfter);

	Optional<CompanyPost> findCompanyPostDetailById(Long postId);

}
