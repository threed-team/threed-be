package com.example.threedbe.post.domain;

import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.ColumnDefault;

import com.example.threedbe.bookmark.domain.Bookmark;
import com.example.threedbe.common.domain.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "posts")
@Entity
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorColumn(name = "post_type")
public abstract class Post extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	@Column(columnDefinition = "TEXT")
	private String content;

	private String thumbnailImageUrl;

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private Field field;

	@Column(nullable = false)
	@ColumnDefault("0")
	private int viewCount;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Bookmark> bookmarks;

	public void increaseViewCount() {
		this.viewCount++;
	}

	public int getBookmarkCount() {
		return this.bookmarks.size();
	}

	protected void update(String title, String content, String thumbnailImageUrl, Field field) {
		this.title = title;
		this.content = content;
		this.thumbnailImageUrl = thumbnailImageUrl;
		this.field = field;
	}

	@Override
	public final boolean equals(Object o) {
		if (!(o instanceof Post post))
			return false;

		return Objects.equals(id, post.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}
