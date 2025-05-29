package com.example.threedbe.post.service;

import static com.example.threedbe.post.domain.Skill.*;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.example.threedbe.post.dto.request.MemberPostImageRequest;
import com.example.threedbe.post.dto.request.MemberPostPopularRequest;
import com.example.threedbe.post.dto.request.MemberPostSaveRequest;
import com.example.threedbe.post.dto.request.MemberPostSearchRequest;
import com.example.threedbe.post.dto.request.MemberPostUpdateRequest;
import com.example.threedbe.post.dto.response.MemberPostDetailResponse;
import com.example.threedbe.post.dto.response.MemberPostEditResponse;
import com.example.threedbe.post.dto.response.MemberPostResponse;
import com.example.threedbe.post.dto.response.MemberPostSaveResponse;
import com.example.threedbe.post.dto.response.MemberPostUpdateResponse;
import com.example.threedbe.post.dto.response.PresignedUrlResponse;
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
	public PresignedUrlResponse generateImageUrl(Member member, Long postId, MemberPostImageRequest request) {
		MemberPost memberPost = findMemberPostById(postId);

		if (memberPost.isNotAuthor(member)) {
			throw new ThreedBadRequestException("회원 포스트 작성자가 아닙니다: " + postId);
		}

		PresignedUrlResponse presignedUrlResponse = s3Service.generatePresignedUrl(postId, request.fileName());

		memberPost.addImage(presignedUrlResponse.fileUrl());

		return presignedUrlResponse;
	}

	@Transactional
	public MemberPostSaveResponse save(Member member, Long postId, MemberPostSaveRequest request) {
		MemberPost memberPost = findMemberPostById(postId);

		if (memberPost.isNotAuthor(member)) {
			throw new ThreedBadRequestException("회원 포스트 작성자가 아닙니다: " + postId);
		}

		Field field = Field.fromName(request.field());
		List<Skill> skills = request.skills()
			.stream()
			.map(skillName -> skillRepository.findByName(skillName)
				.orElseGet(() -> skillRepository.save(new Skill(skillName))))
			.toList();

		if (memberPost.isNotDraft()) {
			throw new ThreedBadRequestException("이미 릴리즈된 포스트입니다: " + postId);
		}

		String title = request.title();
		BufferedImage thumbnailImage = thumbnailService.createThumbnailImage(title);
		String thumbnailUrl = s3Service.uploadThumbnailImage(thumbnailImage, title);
		memberPost.release(title, request.content(), field, thumbnailUrl, skills);

		return MemberPostSaveResponse.from(memberPost);
	}

	// TODO: QueryDSL로 변경
	public PageResponse<MemberPostResponse> search(MemberPostSearchRequest request) {
		List<Field> fields = Field.fromNames(request.fields());
		List<String> skillNames = Optional.ofNullable(request.skills())
			.orElse(Collections.emptyList())
			.stream()
			.filter(name -> {
				if (MAIN_SKILLS.contains(name)) {
					return true;
				}

				throw new ThreedNotFoundException("등록된 기술이 아닙니다: " + name);
			})
			.toList();

		Pageable pageable = request.toPageRequest();
		String keyword = request.extractKeyword();
		LocalDateTime startDate = PopularCondition.WEEK.calculateStartDate(LocalDateTime.now());
		List<MemberPost> popularPosts = memberPostRepository.findPopularPosts(startDate);
		LocalDateTime now = LocalDateTime.now();
		Page<MemberPostResponse> memberPostResponses;
		if (skillNames.isEmpty()) {
			if (fields.isEmpty()) {
				memberPostResponses =
					memberPostRepository.searchMemberPostsAll(keyword, pageable)
						.map(post -> {
							boolean isNew = post.getPublishedAt().isAfter(now.minusDays(7));
							boolean isHot = popularPosts.contains(post);

							return MemberPostResponse.from(post, isNew, isHot);
						});
			} else {
				memberPostResponses =
					memberPostRepository.searchMemberPostsWithFieldsAllCompanies(fields, keyword, pageable)
						.map(post -> {
							boolean isNew = post.getPublishedAt().isAfter(now.minusDays(7));
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
							pageable)
						.map(post -> {
							boolean isNew = post.getPublishedAt().isAfter(now.minusDays(7));
							boolean isHot = popularPosts.contains(post);

							return MemberPostResponse.from(post, isNew, isHot);
						});
			} else {
				memberPostResponses =
					memberPostRepository.searchMemberPostsWithFieldsExcludeCompanies(
							fields,
							targetSkillNames,
							keyword,
							pageable)
						.map(post -> {
							boolean isNew = post.getPublishedAt().isAfter(now.minusDays(7));
							boolean isHot = popularPosts.contains(post);

							return MemberPostResponse.from(post, isNew, isHot);
						});
			}
		} else {
			if (fields.isEmpty()) {
				memberPostResponses =
					memberPostRepository.searchMemberPostsWithoutFields(skillNames, keyword, pageable)
						.map(post -> {
							boolean isNew = post.getPublishedAt().isAfter(now.minusDays(7));
							boolean isHot = popularPosts.contains(post);

							return MemberPostResponse.from(post, isNew, isHot);
						});
			} else {
				memberPostResponses =
					memberPostRepository.searchMemberPostsWithFields(fields, skillNames, keyword, pageable)
						.map(post -> {
							boolean isNew = post.getPublishedAt().isAfter(now.minusDays(7));
							boolean isHot = popularPosts.contains(post);

							return MemberPostResponse.from(post, isNew, isHot);
						});
			}
		}

		return PageResponse.from(memberPostResponses);
	}

	@Transactional
	public MemberPostDetailResponse findMemberPostDetail(Member member, Long postId) {
		MemberPost memberPost = findMemberPostDetailById(postId);
		memberPost.increaseViewCount();

		int bookmarkCount = memberPost.getBookmarkCount();
		boolean isBookmarked = memberPost.isBookmarkedBy(member);
		boolean isMyPost = memberPost.isAuthor(member);

		LocalDateTime publishedAt = memberPost.getPublishedAt();
		Long nextId = memberPostRepository.findNextId(publishedAt)
			.orElse(null);
		Long prevId = memberPostRepository.findPreviousId(publishedAt)
			.orElse(null);

		return MemberPostDetailResponse.from(memberPost, bookmarkCount, isBookmarked, isMyPost, nextId, prevId);
	}

	public MemberPostEditResponse findMemberPostForEdit(Member member, Long postId) {
		MemberPost memberPost = memberPostRepository.findByIdAndDeletedAtIsNull(postId)
			.orElseThrow(() -> new ThreedNotFoundException("회원 포스트가 존재하지 않습니다: " + postId));

		if (memberPost.isNotAuthor(member)) {
			throw new ThreedBadRequestException("회원 포스트 작성자가 아닙니다: " + postId);
		}

		return MemberPostEditResponse.from(memberPost);
	}

	public ListResponse<MemberPostResponse> findPopularMemberPosts(MemberPostPopularRequest request) {
		PopularCondition condition = PopularCondition.fromName(request.condition());

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startDate = condition.calculateStartDate(now);

		List<MemberPost> popularPosts = memberPostRepository.findPopularPosts(startDate);

		List<MemberPostResponse> posts = popularPosts.stream()
			.map(post -> toMemberPostResponse(post, now))
			.toList();

		return ListResponse.from(posts);
	}

	@Transactional
	public MemberPostUpdateResponse update(Member member, Long postId, MemberPostUpdateRequest request) {
		MemberPost memberPost = findMemberPostByIdNotDeleted(postId);

		if (!memberPost.getMember().equals(member)) {
			throw new ThreedBadRequestException("회원 포스트 작성자가 아닙니다: " + postId);
		}

		Field field = Field.of(request.field())
			.orElseThrow(() -> new ThreedNotFoundException("등록된 분야가 아닙니다: " + request.field()));
		List<Skill> skills = request.skills()
			.stream()
			.map(skillName -> skillRepository.findByName(skillName)
				.orElseGet(() -> skillRepository.save(new Skill(skillName))))
			.toList();

		if (memberPost.isDraft()) {
			throw new ThreedBadRequestException("릴리즈 전 포스트는 수정할 수 없습니다: " + postId);
		}

		String newTitle = request.title();
		String thumbnailUrl = memberPost.getThumbnailImageUrl();
		if (!newTitle.equals(memberPost.getTitle())) {
			if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
				s3Service.deleteThumbnail(thumbnailUrl);
			}

			BufferedImage thumbnailImage = thumbnailService.createThumbnailImage(newTitle);
			thumbnailUrl = s3Service.uploadThumbnailImage(thumbnailImage, newTitle);
		}

		memberPost.update(newTitle, request.content(), field, thumbnailUrl, skills);

		return MemberPostUpdateResponse.from(memberPost);
	}

	@Transactional
	public void delete(Member member, Long postId) {
		MemberPost memberPost = findMemberPostByIdNotDeleted(postId);

		if (memberPost.isNotAuthor(member)) {
			throw new ThreedBadRequestException("회원 포스트 작성자가 아닙니다: " + postId);
		}

		if (memberPost.isDraft()) {
			throw new ThreedBadRequestException("릴리즈 전 포스트는 삭제할 수 없습니다: " + postId);
		}

		memberPostRepository.delete(memberPost);
	}

	private MemberPost findMemberPostById(Long postId) {
		return memberPostRepository.findById(postId)
			.orElseThrow(() -> new ThreedNotFoundException("회원 포스트가 존재하지 않습니다: " + postId));
	}

	private MemberPost findMemberPostByIdNotDeleted(Long postId) {
		return memberPostRepository.findByIdAndDeletedAtIsNull(postId)
			.orElseThrow(() -> new ThreedNotFoundException("회원 포스트가 존재하지 않습니다: " + postId));
	}

	private MemberPost findMemberPostDetailById(Long postId) {
		return memberPostRepository.findMemberPostDetailById(postId)
			.orElseThrow(() -> new ThreedNotFoundException("회사 포스트가 존재하지 않습니다: " + postId));
	}

	private MemberPostResponse toMemberPostResponse(MemberPost post, LocalDateTime now) {
		boolean isNew = post.isNew(now);

		return MemberPostResponse.from(post, isNew, true);
	}

}
