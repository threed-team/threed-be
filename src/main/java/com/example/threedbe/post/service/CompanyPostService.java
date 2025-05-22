package com.example.threedbe.post.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.threedbe.common.dto.ListResponse;
import com.example.threedbe.common.dto.PageResponse;
import com.example.threedbe.common.exception.ThreedBadRequestException;
import com.example.threedbe.common.exception.ThreedNotFoundException;
import com.example.threedbe.member.domain.Member;
import com.example.threedbe.post.domain.Company;
import com.example.threedbe.post.domain.CompanyPost;
import com.example.threedbe.post.domain.Field;
import com.example.threedbe.post.domain.PopularCondition;
import com.example.threedbe.post.dto.request.CompanyPostPopularRequest;
import com.example.threedbe.post.dto.request.CompanyPostSearchRequest;
import com.example.threedbe.post.dto.response.CompanyPostDetailResponse;
import com.example.threedbe.post.dto.response.CompanyPostResponse;
import com.example.threedbe.post.repository.CompanyPostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyPostService {

	private final CompanyPostRepository companyPostRepository;

	// TODO: QueryDSL로 변경
	public PageResponse<CompanyPostResponse> search(CompanyPostSearchRequest companyPostSearchRequest) {
		List<Field> fields = Optional.ofNullable(companyPostSearchRequest.fields())
			.orElse(Collections.emptyList())
			.stream()
			.map(name -> Field.of(name).orElseThrow(() -> new ThreedNotFoundException("등록된 분야가 아닙니다: " + name)))
			.toList();
		List<Company> companies = Optional.ofNullable(companyPostSearchRequest.companies())
			.orElse(Collections.emptyList())
			.stream()
			.map(name -> Company.of(name).orElseThrow(() -> new ThreedNotFoundException("등록된 회사가 아닙니다: " + name)))
			.toList();

		PageRequest pageRequest = PageRequest.of(companyPostSearchRequest.page() - 1, companyPostSearchRequest.size());
		String keyword =
			StringUtils.hasText(companyPostSearchRequest.keyword()) ? companyPostSearchRequest.keyword() : null;
		LocalDateTime startDate = PopularCondition.WEEK.calculateStartDate(LocalDateTime.now());
		List<CompanyPost> popularPosts = companyPostRepository.findCompanyPostsOrderByPopularity(startDate);
		LocalDateTime now = LocalDateTime.now();
		Page<CompanyPostResponse> companyPostResponses;
		if (companies.isEmpty()) {
			if (fields.isEmpty()) {
				companyPostResponses =
					companyPostRepository.searchCompanyPostsAll(keyword, pageRequest)
						.map(post -> {
							boolean isNew = post.getPublishedAt().isAfter(now.minusDays(7));
							boolean isHot = popularPosts.contains(post);

							return CompanyPostResponse.from(post, isNew, isHot);
						});
			} else {
				companyPostResponses =
					companyPostRepository.searchCompanyPostsWithFieldsAllCompanies(fields, keyword, pageRequest)
						.map(post -> {
							boolean isNew = post.getPublishedAt().isAfter(now.minusDays(7));
							boolean isHot = popularPosts.contains(post);

							return CompanyPostResponse.from(post, isNew, isHot);
						});
			}
		} else if (companies.contains(Company.ETC)) {
			List<Company> excludeCompanies = new ArrayList<>(Company.MAIN_COMPANIES);
			companies.stream()
				.filter(company -> company != Company.ETC)
				.forEach(excludeCompanies::remove);

			if (fields.isEmpty()) {
				companyPostResponses = companyPostRepository.searchCompanyPostsWithoutFieldsExcludeCompanies(
						excludeCompanies,
						keyword,
						pageRequest)
					.map(post -> {
						boolean isNew = post.getPublishedAt().isAfter(now.minusDays(7));
						boolean isHot = popularPosts.contains(post);

						return CompanyPostResponse.from(post, isNew, isHot);
					});
			} else {
				companyPostResponses = companyPostRepository.searchCompanyPostsWithFieldsExcludeCompanies(
						fields,
						excludeCompanies,
						keyword,
						pageRequest)
					.map(post -> {
						boolean isNew = post.getPublishedAt().isAfter(now.minusDays(7));
						boolean isHot = popularPosts.contains(post);

						return CompanyPostResponse.from(post, isNew, isHot);
					});
			}
		} else {
			if (fields.isEmpty()) {
				companyPostResponses =
					companyPostRepository.searchCompanyPostsWithoutFields(companies, keyword, pageRequest)
						.map(post -> {
							boolean isNew = post.getPublishedAt().isAfter(now.minusDays(7));
							boolean isHot = popularPosts.contains(post);

							return CompanyPostResponse.from(post, isNew, isHot);
						});
			} else {
				companyPostResponses =
					companyPostRepository.searchCompanyPostsWithFields(fields, companies, keyword, pageRequest)
						.map(post -> {
							boolean isNew = post.getPublishedAt().isAfter(now.minusDays(7));
							boolean isHot = popularPosts.contains(post);

							return CompanyPostResponse.from(post, isNew, isHot);
						});
			}
		}

		return PageResponse.from(companyPostResponses);
	}

	// TODO: 쿼리 수 줄이기
	@Transactional
	public CompanyPostDetailResponse findCompanyPostDetail(Member member, Long postId) {
		CompanyPost companyPost = companyPostRepository.findById(postId)
			.orElseThrow(() -> new ThreedNotFoundException("회사 포스트가 존재하지 않습니다: " + postId));
		companyPost.increaseViewCount();

		int bookmarkCount = companyPost.getBookmarkCount();

		boolean isBookmarked = companyPost.getBookmarks()
			.stream()
			.anyMatch(bookmark -> bookmark.getMember().equals(member));

		LocalDateTime publishedAt = companyPost.getPublishedAt();
		Long nextId = companyPostRepository.findNextId(publishedAt)
			.orElse(null);
		Long prevId = companyPostRepository.findPrevId(publishedAt)
			.orElse(null);

		return CompanyPostDetailResponse.from(companyPost, bookmarkCount, isBookmarked, nextId, prevId);
	}

	// TODO: QueryDSL로 변경
	public ListResponse<CompanyPostResponse> findPopularCompanyPosts(
		CompanyPostPopularRequest companyPostPopularRequest) {

		PopularCondition condition = PopularCondition.of(companyPostPopularRequest.condition())
			.orElseThrow(() -> new ThreedBadRequestException("잘못된 인기 조건입니다: " + companyPostPopularRequest.condition()));

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startDate = condition.calculateStartDate(now);

		List<CompanyPostResponse> posts = companyPostRepository.findCompanyPostsOrderByPopularity(startDate).stream()
			.map(post -> {
				boolean isNew = post.getPublishedAt().isAfter(now.minusDays(7));

				return CompanyPostResponse.from(post, isNew, true);
			})
			.toList();

		return ListResponse.from(posts);
	}

}
