package com.example.threedbe.post.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.threedbe.bookmark.dto.response.BookmarkedPostResponse;
import com.example.threedbe.common.exception.ThreedNotFoundException;
import com.example.threedbe.post.domain.PopularCondition;
import com.example.threedbe.post.domain.Post;
import com.example.threedbe.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

	private final PostRepository postRepository;
	private final CompanyPostService companyPostService;
	private final MemberPostService memberPostService;

	public Post findById(Long postId) {
		return postRepository.findPostById(postId)
			.orElseThrow(() -> new ThreedNotFoundException("존재하지 않는 포스트입니다."));
	}

	public Page<BookmarkedPostResponse> findBookmarkedPostsByMemberId(Long memberId, Pageable pageable) {
		return postRepository.findBookmarkedPostsByMemberId(memberId, pageable);
	}

	public Page<BookmarkedPostResponse> findBookmarkedPosts(Long memberId, Pageable pageable) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startDate = PopularCondition.WEEK.calculateStartDate(now);
		List<Long> popularCompanyPostIds = companyPostService.findPopularPostIds(startDate);
		List<Long> popularMemberPostIds = memberPostService.findPopularPostIds(startDate);

		Page<BookmarkedPostResponse> bookmarkedPosts = findBookmarkedPostsByMemberId(memberId, pageable);

		return bookmarkedPosts.map(response -> toBookmarkedPostResponse(
			response,
			now,
			popularCompanyPostIds,
			popularMemberPostIds));
	}

	private static BookmarkedPostResponse toBookmarkedPostResponse(
		BookmarkedPostResponse response,
		LocalDateTime now,
		List<Long> popularCompanyPostIds,
		List<Long> popularMemberPostIds) {

		boolean isNew = response.createdAt().isAfter(now);
		boolean isHot = response.isCompany()
			? popularCompanyPostIds.contains(response.id())
			: popularMemberPostIds.contains(response.id());

		return BookmarkedPostResponse.withPopularity(response, isNew, isHot);
	}

}
