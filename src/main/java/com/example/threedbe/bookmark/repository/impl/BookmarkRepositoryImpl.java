package com.example.threedbe.bookmark.repository.impl;

import static com.example.threedbe.bookmark.domain.QBookmark.*;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.threedbe.bookmark.domain.Bookmark;
import com.example.threedbe.bookmark.repository.BookmarkRepositoryCustom;
import com.example.threedbe.member.domain.Member;
import com.example.threedbe.post.domain.Post;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Repository
public class BookmarkRepositoryImpl implements BookmarkRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public BookmarkRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public Optional<Bookmark> findFirstByMemberAndPost(Member member, Post post) {
		Bookmark foundBookmark = queryFactory.selectFrom(bookmark)
			.where(bookmark.member.eq(member), bookmark.post.eq(post))
			.fetchOne();

		return Optional.ofNullable(foundBookmark);
	}
}
