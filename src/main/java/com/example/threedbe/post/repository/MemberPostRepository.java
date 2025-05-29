package com.example.threedbe.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.threedbe.post.domain.Field;
import com.example.threedbe.post.domain.MemberPost;

public interface MemberPostRepository extends JpaRepository<MemberPost, Long>, MemberPostRepositoryCustom {

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

	Optional<MemberPost> findByIdAndDeletedAtIsNull(Long id);

}
