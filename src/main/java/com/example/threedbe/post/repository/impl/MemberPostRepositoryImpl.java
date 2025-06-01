package com.example.threedbe.post.repository.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.example.threedbe.post.domain.Field;
import com.example.threedbe.post.domain.MemberPost;
import com.example.threedbe.post.domain.QMemberPost;
import com.example.threedbe.post.domain.QMemberPostSkill;
import com.example.threedbe.post.domain.QSkill;
import com.example.threedbe.post.repository.MemberPostRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Repository
public class MemberPostRepositoryImpl implements MemberPostRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	private final QMemberPost memberPost = QMemberPost.memberPost;
	private final QMemberPostSkill memberPostSkill = QMemberPostSkill.memberPostSkill;
	private final QSkill skill = QSkill.skill;

	public MemberPostRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public Optional<MemberPost> findMemberPostById(Long postId) {
		MemberPost post = queryFactory.selectFrom(memberPost)
			.where(memberPost.id.eq(postId))
			.fetchFirst();

		return Optional.ofNullable(post);
	}

	@Override
	public Page<MemberPost> searchMemberPosts(
		List<Field> fields,
		List<String> skillNames,
		String keyword,
		boolean excludeSkillNames,
		Pageable pageable) {

		BooleanBuilder whereClause = new BooleanBuilder().and(memberPost.publishedAt.isNotNull())
			.and(fieldsIn(fields))
			.and(skillNamesFilter(skillNames, excludeSkillNames))
			.and(keywordContains(keyword));

		List<MemberPost> content = queryFactory.selectFrom(memberPost)
			.leftJoin(memberPost.member).fetchJoin()
			.leftJoin(memberPost.skills, memberPostSkill).fetchJoin()
			.leftJoin(memberPostSkill.skill, skill).fetchJoin()
			.where(whereClause)
			.orderBy(memberPost.publishedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory.select(memberPost.count())
			.from(memberPost)
			.where(whereClause);

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	@Override
	public List<Long> findPopularPostIds(LocalDateTime publishedAfter) {
		return queryFactory.select(memberPost.id)
			.from(memberPost)
			.where(memberPost.publishedAt.after(publishedAfter))
			.orderBy(memberPost.viewCount.add(memberPost.bookmarks.size().multiply(2L)).desc())
			.limit(10)
			.fetch();
	}

	@Override
	public Optional<Long> findNextId(LocalDateTime publishedAt) {
		Long postId = queryFactory.select(memberPost.id)
			.from(memberPost)
			.where(memberPost.publishedAt.lt(publishedAt))
			.orderBy(memberPost.publishedAt.desc())
			.fetchFirst();

		return Optional.ofNullable(postId);
	}

	@Override
	public Optional<Long> findPreviousId(LocalDateTime publishedAt) {
		Long postId = queryFactory.select(memberPost.id)
			.from(memberPost)
			.where(memberPost.publishedAt.gt(publishedAt))
			.orderBy(memberPost.publishedAt.asc())
			.fetchFirst();

		return Optional.ofNullable(postId);
	}

	@Override
	public List<MemberPost> findPopularPosts(LocalDateTime publishedAfter) {
		OrderSpecifier<Integer> order = memberPost.viewCount.add(memberPost.bookmarks.size().multiply(2L)).desc();

		JPQLQuery<Long> subQuery = JPAExpressions.select(memberPost.id)
			.from(memberPost)
			.where(memberPost.publishedAt.after(publishedAfter))
			.orderBy(order)
			.limit(10);

		return queryFactory.selectFrom(memberPost)
			.leftJoin(memberPost.member).fetchJoin()
			.leftJoin(memberPost.skills, memberPostSkill).fetchJoin()
			.leftJoin(memberPostSkill.skill, skill).fetchJoin()
			.where(memberPost.id.in(subQuery))
			.orderBy(order)
			.fetch();
	}

	@Override
	public Optional<MemberPost> findMemberPostDetailById(Long postId) {
		MemberPost post = queryFactory.selectFrom(memberPost)
			.leftJoin(memberPost.skills, memberPostSkill).fetchJoin()
			.leftJoin(memberPostSkill.skill, skill).fetchJoin()
			.where(memberPost.id.eq(postId), memberPost.publishedAt.isNotNull())
			.fetchOne();

		return Optional.ofNullable(post);
	}

	@Override
	public Page<MemberPost> findMemberPostsByMemberId(Long memberId, Pageable pageable) {
		BooleanBuilder whereClause = new BooleanBuilder()
			.and(memberPost.member.id.eq(memberId))
			.and(memberPost.publishedAt.isNotNull());

		List<Long> postIds = queryFactory.select(memberPost.id)
			.from(memberPost)
			.where(whereClause)
			.orderBy(memberPost.publishedAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		if (postIds.isEmpty()) {
			return new PageImpl<>(List.of(), pageable, 0);
		}

		List<MemberPost> content = queryFactory.selectFrom(memberPost)
			.join(memberPost.member).fetchJoin()
			.leftJoin(memberPost.skills, memberPostSkill).fetchJoin()
			.leftJoin(memberPostSkill.skill, skill).fetchJoin()
			.where(memberPost.id.in(postIds))
			.distinct()
			.fetch();

		JPAQuery<Long> countQuery = queryFactory.select(memberPost.count())
			.from(memberPost)
			.where(whereClause);

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	private BooleanExpression fieldsIn(List<Field> fields) {
		return fields != null && !fields.isEmpty() ? memberPost.field.in(fields) : null;
	}

	private BooleanExpression skillNamesFilter(List<String> skillNames, boolean exclude) {
		if (skillNames == null || skillNames.isEmpty()) {
			return null;
		}

		JPQLQuery<Long> subQuery = JPAExpressions.select(memberPostSkill.memberPost.id)
			.from(memberPostSkill)
			.join(memberPostSkill.skill, skill)
			.where(skill.name.in(skillNames));

		return exclude ? memberPost.id.notIn(subQuery) : memberPost.id.in(subQuery);
	}

	private BooleanExpression keywordContains(String keyword) {
		return keyword != null ?
			memberPost.title.containsIgnoreCase(keyword)
				.or(memberPost.content.containsIgnoreCase(keyword)) : null;
	}

}
