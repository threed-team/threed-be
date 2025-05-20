package com.example.threedbe.post.domain;

import java.time.LocalDateTime;
import java.util.List;

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

	private LocalDateTime releasedAt;

	public MemberPost(Member member) {
		this.member = member;
	}

}
