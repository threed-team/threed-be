package com.example.threedbe.post.repository.impl;

import static com.example.threedbe.post.domain.QMemberPost.*;
import static com.example.threedbe.post.domain.QMemberPostSkill.*;
import static com.example.threedbe.post.domain.QSkill.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.threedbe.post.domain.MemberPost;
import com.example.threedbe.post.repository.MemberPostRepositoryCustom;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Repository
public class MemberPostRepositoryImpl implements MemberPostRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public MemberPostRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public Optional<Long> findNextId(LocalDateTime publishedAt) {
		return Optional.ofNullable(
			queryFactory.select(memberPost.id)
				.from(memberPost)
				.where(memberPost.publishedAt.lt(publishedAt))
				.orderBy(memberPost.publishedAt.desc())
				.fetchFirst()
		);
	}

	@Override
	public Optional<Long> findPreviousId(LocalDateTime publishedAt) {
		return Optional.ofNullable(
			queryFactory.select(memberPost.id)
				.from(memberPost)
				.where(memberPost.publishedAt.gt(publishedAt))
				.orderBy(memberPost.publishedAt.asc())
				.fetchFirst()
		);
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
			.where(memberPost.id.eq(postId))
			.fetchOne();

		return Optional.ofNullable(post);
	}

}
