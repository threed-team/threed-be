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

import com.example.threedbe.common.dto.PageResponse;
import com.example.threedbe.common.exception.ThreedBadRequestException;
import com.example.threedbe.common.exception.ThreedNotFoundException;
import com.example.threedbe.post.domain.Company;
import com.example.threedbe.post.domain.CompanyPost;
import com.example.threedbe.post.domain.Field;
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
			.map(value -> Field.of(value).orElseThrow(() -> new ThreedBadRequestException("등록된 분야가 아닙니다: " + value)))
			.toList();
		List<Company> companies = Optional.ofNullable(companyPostSearchRequest.companies())
			.orElse(Collections.emptyList())
			.stream()
			.map(value -> Company.of(value).orElseThrow(() -> new ThreedBadRequestException("등록된 회사가 아닙니다: " + value)))
			.toList();
		PageRequest pageRequest = PageRequest.of(companyPostSearchRequest.page() - 1, companyPostSearchRequest.size());
		String keyword =
			StringUtils.hasText(companyPostSearchRequest.keyword()) ? companyPostSearchRequest.keyword() : null;

		Page<CompanyPostResponse> companyPostResponses;
		if (companies.isEmpty()) {
			if (fields.isEmpty()) {
				companyPostResponses
					= companyPostRepository.searchCompanyPostsAll(keyword, pageRequest)
					.map(CompanyPostResponse::from);

			} else {
				companyPostResponses
					= companyPostRepository.searchCompanyPostsWithFieldsAllCompanies(fields, keyword, pageRequest)
					.map(CompanyPostResponse::from);
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
					.map(CompanyPostResponse::from);
			} else {
				companyPostResponses = companyPostRepository.searchCompanyPostsWithFieldsExcludeCompanies(
						fields,
						excludeCompanies,
						keyword,
						pageRequest)
					.map(CompanyPostResponse::from);
			}
		} else {
			if (fields.isEmpty()) {
				companyPostResponses
					= companyPostRepository.searchCompanyPostsWithoutFields(companies, keyword, pageRequest)
					.map(CompanyPostResponse::from);
			} else {
				companyPostResponses
					= companyPostRepository.searchCompanyPostsWithFields(fields, companies, keyword, pageRequest)
					.map(CompanyPostResponse::from);
			}
		}

		return PageResponse.from(companyPostResponses);
	}

	// TODO: 쿼리 수 줄이기
	@Transactional
	public CompanyPostDetailResponse getCompanyPostDetail(Long postId) {
		CompanyPost companyPost = companyPostRepository.findById(postId)
			.orElseThrow(() -> new ThreedNotFoundException("회사 포스트가 존재하지 않습니다: " + postId));
		companyPost.increaseViewCount();

		int bookmarkCount = companyPost.getBookmarkCount();

		LocalDateTime createdAt = companyPost.getCreatedAt();
		Long nextId = companyPostRepository.findNextId(createdAt)
			.orElse(null);
		Long prevId = companyPostRepository.findPrevId(createdAt)
			.orElse(null);

		return CompanyPostDetailResponse.from(companyPost, bookmarkCount, nextId, prevId);
	}

}
