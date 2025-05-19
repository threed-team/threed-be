package com.example.threedbe.post.service;

import static com.example.threedbe.post.domain.Skill.*;

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
import com.example.threedbe.post.domain.Field;
import com.example.threedbe.post.domain.MemberPost;
import com.example.threedbe.post.domain.PopularCondition;
import com.example.threedbe.post.domain.Skill;
import com.example.threedbe.post.dto.request.MemberPostPopularRequest;
import com.example.threedbe.post.dto.request.MemberPostSearchRequest;
import com.example.threedbe.post.dto.response.MemberPostDetailResponse;
import com.example.threedbe.post.dto.response.MemberPostResponse;
import com.example.threedbe.post.repository.MemberPostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberPostService {

	private final MemberPostRepository memberPostRepository;

	// TODO: QueryDSL로 변경
	public PageResponse<MemberPostResponse> search(MemberPostSearchRequest memberPostSearchRequest) {
		List<Field> fields = Optional.ofNullable(memberPostSearchRequest.fields())
			.orElse(Collections.emptyList())
			.stream()
			.map(name -> Field.of(name)
				.orElseThrow(() -> new ThreedNotFoundException("등록된 분야가 아닙니다: " + name)))
			.toList();

		List<String> skillNames = Optional.ofNullable(memberPostSearchRequest.skills())
			.orElse(Collections.emptyList())
			.stream()
			.filter(name -> {
				if (MAIN_SKILLS.contains(name)) {
					return true;
				}

				throw new ThreedNotFoundException("등록된 기술이 아닙니다: " + name);
			})
			.toList();

		PageRequest pageRequest = PageRequest.of(memberPostSearchRequest.page() - 1, memberPostSearchRequest.size());

		String keyword =
			StringUtils.hasText(memberPostSearchRequest.keyword()) ? memberPostSearchRequest.keyword() : null;

		Page<MemberPostResponse> memberPostResponses;
		if (skillNames.isEmpty()) {
			if (fields.isEmpty()) {
				memberPostResponses =
					memberPostRepository.searchMemberPostsAll(keyword, pageRequest)
						.map(MemberPostResponse::from);
			} else {
				memberPostResponses =
					memberPostRepository.searchMemberPostsWithFieldsAllCompanies(fields, keyword, pageRequest)
						.map(MemberPostResponse::from);
			}
		} else if (skillNames.contains(Skill.ETC)) {
			List<String> targetSkillNames = new ArrayList<>(MAIN_SKILLS);
			skillNames.stream()
				.filter(skillName -> !skillName.equals(Skill.ETC))
				.forEach(targetSkillNames::remove);

			if (fields.isEmpty()) {
				memberPostResponses =
					memberPostRepository.searchMemberPostsWithoutFieldsExcludeCompanies(
							targetSkillNames,
							keyword,
							pageRequest)
						.map(MemberPostResponse::from);
			} else {
				memberPostResponses =
					memberPostRepository.searchMemberPostsWithFieldsExcludeCompanies(
							fields,
							targetSkillNames,
							keyword,
							pageRequest)
						.map(MemberPostResponse::from);
			}
		} else {
			if (fields.isEmpty()) {
				memberPostResponses =
					memberPostRepository.searchMemberPostsWithoutFields(skillNames, keyword, pageRequest)
						.map(MemberPostResponse::from);
			} else {
				memberPostResponses =
					memberPostRepository.searchMemberPostsWithFields(fields, skillNames, keyword, pageRequest)
						.map(MemberPostResponse::from);
			}
		}

		return PageResponse.from(memberPostResponses);
	}

	// TODO: 쿼리 수 줄이기
	@Transactional
	public MemberPostDetailResponse findMemberPostDetail(Member member, Long postId) {
		MemberPost memberPost = memberPostRepository.findById(postId)
			.orElseThrow(() -> new ThreedNotFoundException("회원 포스트가 존재하지 않습니다: " + postId));
		memberPost.increaseViewCount();

		int bookmarkCount = memberPost.getBookmarkCount();

		boolean isBookmarked = memberPost.getBookmarks()
			.stream()
			.anyMatch(bookmark -> bookmark.getMember().equals(member));

		boolean isMyPost = memberPost.getMember().equals(member);

		LocalDateTime createdAt = memberPost.getCreatedAt();
		Long nextId = memberPostRepository.findNextId(createdAt)
			.orElse(null);
		Long prevId = memberPostRepository.findPrevId(createdAt)
			.orElse(null);

		return MemberPostDetailResponse.from(memberPost, bookmarkCount, isBookmarked, isMyPost, nextId, prevId);
	}

	// TODO: QueryDSL로 변경
	public ListResponse<MemberPostResponse> findPopularMemberPosts(
		MemberPostPopularRequest memberPostPopularRequest) {

		String conditionName = memberPostPopularRequest.condition();
		PopularCondition condition = PopularCondition.of(conditionName)
			.orElseThrow(() -> new ThreedBadRequestException("잘못된 인기 조건입니다: " + conditionName));

		LocalDateTime startDate = condition.calculateStartDate(LocalDateTime.now());

		List<MemberPostResponse> posts = memberPostRepository.findMemberPostsOrderByPopularity(startDate).stream()
			.map(MemberPostResponse::from)
			.toList();

		return ListResponse.from(posts);
	}

}
