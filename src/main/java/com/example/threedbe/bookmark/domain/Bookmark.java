package com.example.threedbe.bookmark.domain;

import com.example.threedbe.member.domain.Member;
import com.example.threedbe.post.domain.Post;

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

@Table(name = "bookmarks", uniqueConstraints = {@UniqueConstraint(columnNames = {"post_id", "member_id"})})
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false)
	private Post post;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	public Bookmark(Member member, Post post) {
		this.member = member;
		this.post = post;
	}

	public void removeMember() {
		this.member = null;
	}

	public void removePost() {
		this.post = null;
	}

}

