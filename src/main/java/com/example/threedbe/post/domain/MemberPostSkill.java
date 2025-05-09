package com.example.threedbe.post.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "member_post_skills", uniqueConstraints = {@UniqueConstraint(columnNames = {"post_id", "skill_id"})})
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberPostSkill {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false)
	private MemberPost memberPost;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "skill_id", nullable = false)
	private Skill skill;

}
