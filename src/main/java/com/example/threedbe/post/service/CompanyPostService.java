package com.example.threedbe.post.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

	private static final int NEW_POST_DAYS_THRESHOLD = 7;
	private static final PopularCondition DEFAULT_POPULAR_CONDITION = PopularCondition.WEEK;

	public PageResponse<CompanyPostResponse> search(CompanyPostSearchRequest request) {
		List<Field> fields = convertToFields(request.fields());
		List<Company> companies = convertToCompanies(request.companies());
		boolean excludeCompanies = companies.contains(Company.ETC);
		Pageable pageable = createPageRequest(request);
		String keyword = extractKeyword(request);
		Page<CompanyPost> resultPage = companyPostRepository.searchCompanyPosts(
			fields,
			excludeCompanies ? filterExcludedCompanies(companies) : companies,
			keyword,
			excludeCompanies,
			pageable
		);

		LocalDateTime now = LocalDateTime.now();
		List<Long> popularPostIds = findPopularPostIds(now);
		Page<CompanyPostResponse> responsePage =
			resultPage.map(post -> toCompanyPostResponse(post, now, popularPostIds));

		return PageResponse.from(responsePage);
	}

	@Transactional
	public CompanyPostDetailResponse findCompanyPostDetail(Member member, Long postId) {
		CompanyPost companyPost = findCompanyPostById(postId);
		companyPost.increaseViewCount();

		int bookmarkCount = companyPost.getBookmarkCount();
		boolean isBookmarked = isBookmarkedByMember(companyPost, member);

		LocalDateTime publishedAt = companyPost.getPublishedAt();
		Long nextId = findNextPostId(publishedAt);
		Long prevId = findPreviousPostId(publishedAt);

		return CompanyPostDetailResponse.from(companyPost, bookmarkCount, isBookmarked, nextId, prevId);
	}

	public ListResponse<CompanyPostResponse> findPopularCompanyPosts(CompanyPostPopularRequest request) {
		PopularCondition condition = getPopularCondition(request);

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startDate = condition.calculateStartDate(now);

		List<CompanyPost> popularPosts = companyPostRepository.findPopularPosts(startDate);

		List<CompanyPostResponse> responses = popularPosts.stream()
			.map(post -> toCompanyPostResponse(post, now))
			.toList();

		return ListResponse.from(responses);
	}

	private List<Field> convertToFields(List<String> fieldNames) {

		return Optional.ofNullable(fieldNames)
			.orElse(Collections.emptyList())
			.stream()
			.map(this::toField)
			.toList();
	}

	private Field toField(String fieldName) {

		return Field.of(fieldName)
			.orElseThrow(() -> new ThreedNotFoundException("등록된 분야가 아닙니다: " + fieldName));
	}

	private List<Company> convertToCompanies(List<String> companyNames) {

		return Optional.ofNullable(companyNames)
			.orElse(Collections.emptyList())
			.stream()
			.map(this::toCompany)
			.toList();
	}

	private Company toCompany(String companyName) {

		return Company.of(companyName)
			.orElseThrow(() -> new ThreedNotFoundException("등록된 회사가 아닙니다: " + companyName));
	}

	private PageRequest createPageRequest(CompanyPostSearchRequest request) {

		return PageRequest.of(request.page() - 1, request.size());
	}

	private String extractKeyword(CompanyPostSearchRequest request) {

		return StringUtils.hasText(request.keyword()) ? request.keyword() : null;
	}

	private List<Long> findPopularPostIds(LocalDateTime now) {
		LocalDateTime startDate = DEFAULT_POPULAR_CONDITION.calculateStartDate(now);

		return companyPostRepository.findPopularPosts(startDate)
			.stream()
			.map(CompanyPost::getId)
			.toList();
	}

	private List<Company> filterExcludedCompanies(List<Company> companies) {

		return Company.MAIN_COMPANIES
			.stream()
			.filter(company -> !companies.contains(company))
			.toList();
	}

	private CompanyPostResponse toCompanyPostResponse(CompanyPost post, LocalDateTime now, List<Long> popularPostIds) {
		boolean isNew = isNewPost(post.getPublishedAt(), now);
		boolean isHot = popularPostIds.contains(post.getId());

		return CompanyPostResponse.from(post, isNew, isHot);
	}

	private boolean isNewPost(LocalDateTime publishedAt, LocalDateTime now) {

		return publishedAt.isAfter(now.minusDays(NEW_POST_DAYS_THRESHOLD));
	}

	private CompanyPost findCompanyPostById(Long postId) {

		return companyPostRepository.findById(postId)
			.orElseThrow(() -> new ThreedNotFoundException("회사 포스트가 존재하지 않습니다: " + postId));
	}

	private boolean isBookmarkedByMember(CompanyPost post, Member member) {

		return post.getBookmarks()
			.stream()
			.anyMatch(bookmark -> bookmark.getMember().equals(member));
	}

	private Long findNextPostId(LocalDateTime publishedAt) {

		return companyPostRepository.findNextId(publishedAt).orElse(null);
	}

	private Long findPreviousPostId(LocalDateTime publishedAt) {

		return companyPostRepository.findPreviousId(publishedAt).orElse(null);
	}

	private PopularCondition getPopularCondition(CompanyPostPopularRequest request) {

		return PopularCondition.of(request.condition())
			.orElseThrow(() -> new ThreedBadRequestException("잘못된 인기 조건입니다: " + request.condition()));
	}

	private CompanyPostResponse toCompanyPostResponse(CompanyPost post, LocalDateTime now) {
		if (post == null) {
			throw new ThreedBadRequestException("포스트 정보가 없습니다.");
		}

		boolean isNew = isNewPost(post.getPublishedAt(), now);

		return CompanyPostResponse.from(post, isNew, true);
	}

}
