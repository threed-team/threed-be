package com.example.threedbe.post.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "member_post_images")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberPostImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false)
	private MemberPost memberPost;

	@Column(nullable = false)
	private String imageUrl;

	public MemberPostImage(MemberPost memberPost, String imageUrl) {
		this.memberPost = memberPost;
		this.imageUrl = imageUrl;
	}

}
