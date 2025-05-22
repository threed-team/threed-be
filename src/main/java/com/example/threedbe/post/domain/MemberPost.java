package com.example.threedbe.post.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.SQLDelete;

import com.example.threedbe.member.domain.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "member_posts")
@Entity
@Getter
@Filter(name = "deletedPostFilter", condition = "deleted_at IS NULL")
@FilterDef(name = "deletedPostFilter")
@SQLDelete(sql = "UPDATE member_posts SET deleted_at = NOW() WHERE id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("MEMBER")
public class MemberPost extends Post {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@OneToMany(mappedBy = "memberPost", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<MemberPostImage> images;

	@OneToMany(mappedBy = "memberPost", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MemberPostSkill> skills;

	private LocalDateTime deletedAt;

	public MemberPost(Member member) {
		this.member = member;
	}

	public boolean isDraft() {
		return super.publishedAt == null;
	}

	public boolean isNotDraft() {
		return !isDraft();
	}

	public void release(String title, String content, Field field, String thumbnailUrl, List<Skill> skills) {
		super.update(title, content, thumbnailUrl, field);
		skills.forEach(this::addSkill);
		super.publishedAt = LocalDateTime.now();
	}

	public void update(String title, String content, Field field, String thumbnailUrl, List<Skill> skills) {
		super.update(title, content, thumbnailUrl, field);
		this.skills.removeIf(memberPostSkill -> skills.stream()
			.noneMatch(newSkill -> newSkill.getName().equals(memberPostSkill.getSkill().getName())));
		skills.forEach(this::addSkillIfNotExists);
	}

	private void addSkill(Skill skill) {
		this.skills.add(new MemberPostSkill(this, skill));
	}

	private void addSkillIfNotExists(Skill skill) {
		boolean exists = this.skills.stream()
			.anyMatch(memberPostSkill ->
				memberPostSkill.getSkill().getName().equals(skill.getName()));

		if (!exists) {
			addSkill(skill);
		}
	}

}
