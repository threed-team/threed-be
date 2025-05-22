package com.example.threedbe.post.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.threedbe.post.domain.Company;
import com.example.threedbe.post.domain.CompanyPost;
import com.example.threedbe.post.domain.Field;

public interface CompanyPostRepository extends JpaRepository<CompanyPost, Long> {

	boolean existsBySourceUrl(String sourceUrl);

	@Query("SELECT cp FROM CompanyPost cp WHERE " +
		"cp.field IN :fields AND " +
		"cp.company IN :companies AND " +
		"(:keyword IS NULL OR cp.title LIKE %:keyword% OR cp.content LIKE %:keyword%) " +
		"ORDER BY cp.publishedAt DESC")
	Page<CompanyPost> searchCompanyPostsWithFields(
		@Param("fields") List<Field> fields,
		@Param("companies") List<Company> companies,
		@Param("keyword") String keyword,
		Pageable pageable
	);

	@Query("SELECT cp FROM CompanyPost cp WHERE " +
		"cp.company IN :companies AND " +
		"(:keyword IS NULL OR cp.title LIKE %:keyword% OR cp.content LIKE %:keyword%) " +
		"ORDER BY cp.publishedAt DESC")
	Page<CompanyPost> searchCompanyPostsWithoutFields(
		@Param("companies") List<Company> companies,
		@Param("keyword") String keyword,
		Pageable pageable
	);

	@Query("SELECT cp FROM CompanyPost cp WHERE " +
		"cp.field IN :fields AND " +
		"cp.company NOT IN :companies AND " +
		"(:keyword IS NULL OR cp.title LIKE %:keyword% OR cp.content LIKE %:keyword%) " +
		"ORDER BY cp.publishedAt DESC")
	Page<CompanyPost> searchCompanyPostsWithFieldsExcludeCompanies(
		@Param("fields") List<Field> fields,
		@Param("companies") List<Company> companies,
		@Param("keyword") String keyword,
		Pageable pageable
	);

	@Query("SELECT cp FROM CompanyPost cp WHERE " +
		"cp.company NOT IN :companies AND " +
		"(:keyword IS NULL OR cp.title LIKE %:keyword% OR cp.content LIKE %:keyword%) " +
		"ORDER BY cp.publishedAt DESC")
	Page<CompanyPost> searchCompanyPostsWithoutFieldsExcludeCompanies(
		@Param("companies") List<Company> companies,
		@Param("keyword") String keyword,
		Pageable pageable
	);

	@Query("SELECT cp FROM CompanyPost cp WHERE " +
		"cp.field IN :fields AND " +
		"(:keyword IS NULL OR cp.title LIKE %:keyword% OR cp.content LIKE %:keyword%) " +
		"ORDER BY cp.publishedAt DESC")
	Page<CompanyPost> searchCompanyPostsWithFieldsAllCompanies(
		@Param("fields") List<Field> fields,
		@Param("keyword") String keyword,
		Pageable pageable
	);

	@Query("SELECT cp FROM CompanyPost cp WHERE " +
		"(:keyword IS NULL OR cp.title LIKE %:keyword% OR cp.content LIKE %:keyword%) " +
		"ORDER BY cp.publishedAt DESC")
	Page<CompanyPost> searchCompanyPostsAll(
		@Param("keyword") String keyword,
		Pageable pageable
	);

	@Query("SELECT cp.id FROM CompanyPost cp WHERE (cp.publishedAt < :publishedAt) ORDER BY cp.publishedAt DESC LIMIT 1")
	Optional<Long> findNextId(@Param("publishedAt") LocalDateTime publishedAt);

	@Query("SELECT cp.id FROM CompanyPost cp WHERE (cp.publishedAt > :publishedAt) ORDER BY cp.publishedAt ASC LIMIT 1")
	Optional<Long> findPrevId(@Param("publishedAt") LocalDateTime publishedAt);

	@Query("SELECT cp FROM CompanyPost cp WHERE cp.publishedAt > :publishedAt " +
		"ORDER BY (cp.viewCount + SIZE(cp.bookmarks) * 2) DESC LIMIT 10")
	List<CompanyPost> findCompanyPostsOrderByPopularity(@Param("publishedAt") LocalDateTime publishedAt);

}
