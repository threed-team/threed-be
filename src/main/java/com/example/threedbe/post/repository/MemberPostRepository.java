package com.example.threedbe.post.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.threedbe.post.domain.Field;
import com.example.threedbe.post.domain.MemberPost;

public interface MemberPostRepository extends JpaRepository<MemberPost, Long> {

	Page<MemberPost> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

	@Query("SELECT mp FROM MemberPost mp JOIN mp.skills skill WHERE " +
		"mp.field IN :fields AND " +
		"skill.skill.name IN :skillNames AND " +
		"(:keyword IS NULL OR mp.title LIKE %:keyword% OR mp.content LIKE %:keyword%) " +
		"ORDER BY mp.publishedAt DESC")
	Page<MemberPost> searchMemberPostsWithFields(
		@Param("fields") List<Field> fields,
		@Param("skillNames") List<String> skillNames,
		@Param("keyword") String keyword,
		Pageable pageable
	);

	@Query("SELECT mp FROM MemberPost mp JOIN mp.skills skill WHERE " +
		"skill.skill.name IN :skillNames AND " +
		"(:keyword IS NULL OR mp.title LIKE %:keyword% OR mp.content LIKE %:keyword%) " +
		"ORDER BY mp.publishedAt DESC")
	Page<MemberPost> searchMemberPostsWithoutFields(
		@Param("skillNames") List<String> skillNames,
		@Param("keyword") String keyword,
		Pageable pageable
	);

	@Query("SELECT mp FROM MemberPost mp JOIN mp.skills skill WHERE " +
		"mp.field IN :fields AND " +
		"skill.skill.name NOT IN :skillNames AND " +
		"(:keyword IS NULL OR mp.title LIKE %:keyword% OR mp.content LIKE %:keyword%) " +
		"ORDER BY mp.publishedAt DESC")
	Page<MemberPost> searchMemberPostsWithFieldsExcludeCompanies(
		@Param("fields") List<Field> fields,
		@Param("skillNames") List<String> skillNames,
		@Param("keyword") String keyword,
		Pageable pageable
	);

	@Query("SELECT mp FROM MemberPost mp JOIN mp.skills skill WHERE " +
		"skill.skill.name NOT IN :skillNames AND " +
		"(:keyword IS NULL OR mp.title LIKE %:keyword% OR mp.content LIKE %:keyword%) " +
		"ORDER BY mp.publishedAt DESC")
	Page<MemberPost> searchMemberPostsWithoutFieldsExcludeCompanies(
		@Param("skillNames") List<String> skillNames,
		@Param("keyword") String keyword,
		Pageable pageable
	);

	@Query("SELECT mp FROM MemberPost mp WHERE " +
		"mp.field IN :fields AND " +
		"(:keyword IS NULL OR mp.title LIKE %:keyword% OR mp.content LIKE %:keyword%) " +
		"ORDER BY mp.publishedAt DESC")
	Page<MemberPost> searchMemberPostsWithFieldsAllCompanies(
		@Param("fields") List<Field> fields,
		@Param("keyword") String keyword,
		Pageable pageable
	);

	@Query("SELECT mp FROM MemberPost mp WHERE " +
		"(:keyword IS NULL OR mp.title LIKE %:keyword% OR mp.content LIKE %:keyword%) " +
		"ORDER BY mp.publishedAt DESC")
	Page<MemberPost> searchMemberPostsAll(
		@Param("keyword") String keyword,
		Pageable pageable
	);

	@Query("SELECT mp.id FROM MemberPost mp WHERE (mp.publishedAt < :publishedAt) ORDER BY mp.publishedAt DESC LIMIT 1")
	Optional<Long> findNextId(@Param("publishedAt") LocalDateTime publishedAt);

	@Query("SELECT mp.id FROM MemberPost mp WHERE (mp.publishedAt > :publishedAt) ORDER BY mp.publishedAt ASC LIMIT 1")
	Optional<Long> findPrevId(@Param("publishedAt") LocalDateTime publishedAt);

	@Query("SELECT mp FROM MemberPost mp WHERE mp.publishedAt > :publishedAt " +
		"ORDER BY (mp.viewCount + SIZE(mp.bookmarks) * 2) DESC LIMIT 10")
	List<MemberPost> findMemberPostsOrderByPopularity(@Param("publishedAt") LocalDateTime publishedAt);

	Optional<MemberPost> findByIdAndDeletedAtIsNull(Long id);

}
