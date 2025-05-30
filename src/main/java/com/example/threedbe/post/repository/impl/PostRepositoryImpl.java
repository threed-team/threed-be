package com.example.threedbe.post.repository.impl;

import static com.example.threedbe.post.domain.QMemberPost.*;
import static com.example.threedbe.post.domain.QPost.*;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.threedbe.post.domain.MemberPost;
import com.example.threedbe.post.domain.Post;
import com.example.threedbe.post.repository.PostRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Repository
public class PostRepositoryImpl implements PostRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public PostRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public Optional<Post> findPostById(Long postId) {
		BooleanExpression isNotDeletedMemberPost = JPAExpressions
			.selectOne()
			.from(memberPost)
			.where(memberPost.id.eq(postId), memberPost.deletedAt.isNull())
			.exists();

		Post foundPost = queryFactory.selectFrom(post)
			.where(
				post.id.eq(postId),
				post.publishedAt.isNotNull(),
				post.instanceOf(MemberPost.class).not().or(isNotDeletedMemberPost))
			.fetchFirst();

		return Optional.ofNullable(foundPost);
	}

}
