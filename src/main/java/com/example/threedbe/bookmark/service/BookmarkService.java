package com.example.threedbe.bookmark.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.threedbe.bookmark.domain.Bookmark;
import com.example.threedbe.bookmark.dto.request.BookmarkedPostRequest;
import com.example.threedbe.bookmark.dto.response.BookmarkedPostResponse;
import com.example.threedbe.bookmark.repository.BookmarkRepository;
import com.example.threedbe.common.dto.PageResponse;
import com.example.threedbe.common.exception.ThreedBadRequestException;
import com.example.threedbe.common.exception.ThreedConflictException;
import com.example.threedbe.common.exception.ThreedNotFoundException;
import com.example.threedbe.member.domain.Member;
import com.example.threedbe.post.domain.CompanyPost;
import com.example.threedbe.post.domain.MemberPost;
import com.example.threedbe.post.domain.PopularCondition;
import com.example.threedbe.post.domain.Post;
import com.example.threedbe.post.repository.CompanyPostRepository;
import com.example.threedbe.post.repository.MemberPostRepository;
import com.example.threedbe.post.repository.PostRepository;
import com.example.threedbe.post.service.PostService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

	private final BookmarkRepository bookmarkRepository;
	private final PostService postService;
	private final PostRepository postRepository;
	private final CompanyPostRepository companyPostRepository;
	private final MemberPostRepository memberPostRepository;

	@Transactional
	public void createBookmark(Member member, Long postId) {
		Post post = postService.findById(postId);

		bookmarkRepository.findFirstByMemberAndPost(member, post)
			.ifPresent(bookmark -> {
				throw new ThreedConflictException("이미 북마크한 포스트입니다.");
			});

		member.addBookmark(post);
	}

	@Transactional
	public void deleteBookmark(Member member, Long postId) {
		Post post = postService.findById(postId);

		Bookmark bookmark = bookmarkRepository.findFirstByMemberAndPost(member, post)
			.orElseThrow(() -> new ThreedNotFoundException("북마크하지 않은 포스트입니다."));

		member.removeBookmark(bookmark);
	}

	// TODO: N+1 문제 해결하기
	public PageResponse<BookmarkedPostResponse> findBookmarkedPosts(
		Member member,
		BookmarkedPostRequest bookmarkedPostRequest) {

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startDate = PopularCondition.WEEK.calculateStartDate(LocalDateTime.now());

		PageRequest pageRequest = PageRequest.of(bookmarkedPostRequest.page() - 1, bookmarkedPostRequest.size());
		Page<BookmarkedPostResponse> bookmarkedPosts = postRepository.findByBookmarksMemberIdOrderByPublishedAtDesc(
				member.getId(),
				pageRequest)
			.map(post -> {
				if (post instanceof CompanyPost companyPost) {
					boolean isNew = post.getPublishedAt().isAfter(now.minusDays(7));

					List<CompanyPost> popularPosts = companyPostRepository.findPopularPosts(startDate);
					boolean isHot = popularPosts.contains(companyPost);

					return BookmarkedPostResponse.from(companyPost, isNew, isHot);
				} else if (post instanceof MemberPost memberPost) {
					boolean isNew = post.getPublishedAt().isAfter(now.minusDays(7));

					List<MemberPost> popularPosts = memberPostRepository.findPopularPosts(startDate);
					boolean isHot = popularPosts.contains(memberPost);

					return BookmarkedPostResponse.from(memberPost, isNew, isHot);
				} else {
					throw new ThreedBadRequestException("지원하지 않는 게시글 타입입니다.");
				}
			});

		return PageResponse.from(bookmarkedPosts);
	}

}
