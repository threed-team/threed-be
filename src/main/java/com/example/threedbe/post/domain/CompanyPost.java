package com.example.threedbe.post.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "company_posts")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("COMPANY")
public class CompanyPost extends Post {

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private Company company;

	private String sourceUrl;

	public CompanyPost(String title, String content, String thumbnailImageUrl, Field field, LocalDateTime publishedAt,
		Company company,
		String sourceUrl) {
		super(title, content, thumbnailImageUrl, field, publishedAt);
		this.company = company;
		this.sourceUrl = sourceUrl;
	}

}
