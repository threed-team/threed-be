package com.example.threedbe.post.repository.impl;

import static com.example.threedbe.post.domain.QCompanyPost.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.example.threedbe.post.domain.Company;
import com.example.threedbe.post.domain.CompanyPost;
import com.example.threedbe.post.domain.Field;
import com.example.threedbe.post.repository.CompanyPostRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Repository
public class CompanyPostRepositoryImpl implements CompanyPostRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public CompanyPostRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public Page<CompanyPost> searchCompanyPosts(
		List<Field> fields,
		List<Company> companies,
		String keyword,
		boolean excludeCompanies,
		Pageable pageable) {

		JPAQuery<CompanyPost> query = queryFactory
			.selectFrom(companyPost)
			.where(fieldsIn(fields), companiesFilter(companies, excludeCompanies), keywordContains(keyword))
			.orderBy(companyPost.publishedAt.desc());

		List<CompanyPost> content = query
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(companyPost.count())
			.from(companyPost)
			.where(
				fieldsIn(fields),
				companiesFilter(companies, excludeCompanies),
				keywordContains(keyword)
			);

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	@Override
	public List<CompanyPost> findPopularPosts(LocalDateTime publishedAfter) {
		return queryFactory
			.selectFrom(companyPost)
			.where(companyPost.publishedAt.after(publishedAfter))
			.orderBy(companyPost.viewCount.add(companyPost.bookmarks.size().multiply(2L)).desc())
			.limit(10)
			.fetch();
	}

	private BooleanExpression fieldsIn(List<Field> fields) {
		return fields != null && !fields.isEmpty() ?
			companyPost.field.in(fields) : null;
	}

	private BooleanExpression companiesFilter(List<Company> companies, boolean exclude) {
		if (companies == null || companies.isEmpty()) {
			return null;
		}

		return exclude ? companyPost.company.notIn(companies) : companyPost.company.in(companies);
	}

	private BooleanExpression keywordContains(String keyword) {

		return keyword != null ?
			companyPost.title.containsIgnoreCase(keyword)
				.or(companyPost.content.containsIgnoreCase(keyword)) : null;
	}

}
