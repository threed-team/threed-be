package com.example.threedbe.post.service;

import static com.example.threedbe.post.domain.Skill.*;

import java.awt.image.BufferedImage;
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
import com.example.threedbe.common.service.S3Service;
import com.example.threedbe.member.domain.Member;
import com.example.threedbe.post.domain.Field;
import com.example.threedbe.post.domain.MemberPost;
import com.example.threedbe.post.domain.PopularCondition;
import com.example.threedbe.post.domain.Skill;
import com.example.threedbe.post.dto.request.MemberPostPopularRequest;
import com.example.threedbe.post.dto.request.MemberPostSaveRequest;
import com.example.threedbe.post.dto.request.MemberPostSearchRequest;
import com.example.threedbe.post.dto.request.MemberPostUpdateRequest;
import com.example.threedbe.post.dto.response.MemberPostDetailResponse;
import com.example.threedbe.post.dto.response.MemberPostResponse;
import com.example.threedbe.post.dto.response.MemberPostSaveResponse;
import com.example.threedbe.post.dto.response.MemberPostUpdateResponse;
import com.example.threedbe.post.repository.MemberPostRepository;
import com.example.threedbe.post.repository.SkillRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberPostService {

	private final MemberPostRepository memberPostRepository;
	private final SkillRepository skillRepository;
	private final ThumbnailService thumbnailService;
	private final S3Service s3Service;

	@Transactional
	public MemberPostSaveResponse saveDraft(Member member) {
		MemberPost memberPost = memberPostRepository.save(new MemberPost(member));

		return MemberPostSaveResponse.from(memberPost);
	}

	@Transactional
	public MemberPostSaveResponse save(Member member, Long postId, MemberPostSaveRequest memberPostSaveRequest) {
		MemberPost memberPost = memberPostRepository.findById(postId)
			.orElseThrow(() -> new ThreedNotFoundException("회원 포스트가 존재하지 않습니다: " + postId));

		if (!memberPost.getMember().equals(member)) {
			throw new ThreedBadRequestException("회원 포스트 작성자가 아닙니다: " + postId);
		}

		Field field = Field.of(memberPostSaveRequest.field())
			.orElseThrow(() -> new ThreedNotFoundException("등록된 분야가 아닙니다: " + memberPostSaveRequest.field()));
		List<Skill> skills = memberPostSaveRequest.skills()
			.stream()
			.map(skillName -> skillRepository.findByName(skillName)
				.orElseGet(() -> skillRepository.save(new Skill(skillName))))
			.toList();

		if (memberPost.isNotDraft()) {
			throw new ThreedBadRequestException("이미 릴리즈된 포스트입니다: " + postId);
		}

		String title = memberPostSaveRequest.title();
		BufferedImage thumbnailImage = thumbnailService.createThumbnailImage(title);
		String thumbnailUrl = s3Service.uploadThumbnailImage(thumbnailImage, title);
		memberPost.release(title, memberPostSaveRequest.content(), field, thumbnailUrl, skills);

		return MemberPostSaveResponse.from(memberPost);
	}

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
		LocalDateTime startDate = PopularCondition.WEEK.calculateStartDate(LocalDateTime.now());
		List<MemberPost> popularPosts = memberPostRepository.findMemberPostsOrderByPopularity(startDate);
		LocalDateTime now = LocalDateTime.now();
		Page<MemberPostResponse> memberPostResponses;
		if (skillNames.isEmpty()) {
			if (fields.isEmpty()) {
				memberPostResponses =
					memberPostRepository.searchMemberPostsAll(keyword, pageRequest)
						.map(post -> {
							boolean isNew = post.getCreatedAt().isAfter(now.minusDays(7));
							boolean isHot = popularPosts.contains(post);

							return MemberPostResponse.from(post, isNew, isHot);
						});
			} else {
				memberPostResponses =
					memberPostRepository.searchMemberPostsWithFieldsAllCompanies(fields, keyword, pageRequest)
						.map(post -> {
							boolean isNew = post.getCreatedAt().isAfter(now.minusDays(7));
							boolean isHot = popularPosts.contains(post);

							return MemberPostResponse.from(post, isNew, isHot);
						});
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
						.map(post -> {
							boolean isNew = post.getCreatedAt().isAfter(now.minusDays(7));
							boolean isHot = popularPosts.contains(post);

							return MemberPostResponse.from(post, isNew, isHot);
						});
			} else {
				memberPostResponses =
					memberPostRepository.searchMemberPostsWithFieldsExcludeCompanies(
							fields,
							targetSkillNames,
							keyword,
							pageRequest)
						.map(post -> {
							boolean isNew = post.getCreatedAt().isAfter(now.minusDays(7));
							boolean isHot = popularPosts.contains(post);

							return MemberPostResponse.from(post, isNew, isHot);
						});
			}
		} else {
			if (fields.isEmpty()) {
				memberPostResponses =
					memberPostRepository.searchMemberPostsWithoutFields(skillNames, keyword, pageRequest)
						.map(post -> {
							boolean isNew = post.getCreatedAt().isAfter(now.minusDays(7));
							boolean isHot = popularPosts.contains(post);

							return MemberPostResponse.from(post, isNew, isHot);
						});
			} else {
				memberPostResponses =
					memberPostRepository.searchMemberPostsWithFields(fields, skillNames, keyword, pageRequest)
						.map(post -> {
							boolean isNew = post.getCreatedAt().isAfter(now.minusDays(7));
							boolean isHot = popularPosts.contains(post);

							return MemberPostResponse.from(post, isNew, isHot);
						});
			}
		}

		return PageResponse.from(memberPostResponses);
	}

	// TODO: 쿼리 수 줄이기
	@Transactional
	public MemberPostDetailResponse findMemberPostDetail(Member member, Long postId) {
		MemberPost memberPost = memberPostRepository.findByIdAndDeletedAtIsNull(postId)
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

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startDate = condition.calculateStartDate(now);

		List<MemberPostResponse> posts = memberPostRepository.findMemberPostsOrderByPopularity(startDate).stream()
			.map(post -> {
				boolean isNew = post.getCreatedAt().isAfter(now.minusDays(7));

				return MemberPostResponse.from(post, isNew, true);
			})
			.toList();

		return ListResponse.from(posts);
	}

	@Transactional
	public MemberPostUpdateResponse update(
		Member member,
		Long postId,
		MemberPostUpdateRequest memberPostUpdateRequest) {

		MemberPost memberPost = memberPostRepository.findByIdAndDeletedAtIsNull(postId)
			.orElseThrow(() -> new ThreedNotFoundException("회원 포스트가 존재하지 않습니다: " + postId));

		if (!memberPost.getMember().equals(member)) {
			throw new ThreedBadRequestException("회원 포스트 작성자가 아닙니다: " + postId);
		}

		Field field = Field.of(memberPostUpdateRequest.field())
			.orElseThrow(() -> new ThreedNotFoundException("등록된 분야가 아닙니다: " + memberPostUpdateRequest.field()));
		List<Skill> skills = memberPostUpdateRequest.skills()
			.stream()
			.map(skillName -> skillRepository.findByName(skillName)
				.orElseGet(() -> skillRepository.save(new Skill(skillName))))
			.toList();

		if (memberPost.isDraft()) {
			throw new ThreedBadRequestException("릴리즈 전 포스트는 수정할 수 없습니다: " + postId);
		}

		String newTitle = memberPostUpdateRequest.title();
		String thumbnailUrl = memberPost.getThumbnailImageUrl();
		if (!newTitle.equals(memberPost.getTitle())) {
			if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
				s3Service.deleteThumbnail(thumbnailUrl);
			}

			BufferedImage thumbnailImage = thumbnailService.createThumbnailImage(newTitle);
			thumbnailUrl = s3Service.uploadThumbnailImage(thumbnailImage, newTitle);
		}

		memberPost.update(newTitle, memberPostUpdateRequest.content(), field, thumbnailUrl, skills);

		return MemberPostUpdateResponse.from(memberPost);
	}

}
