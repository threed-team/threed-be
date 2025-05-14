package com.example.threedbe.member.domain;

import java.time.LocalDateTime;
import java.util.List;

import com.example.threedbe.auth.domain.RefreshToken;
import com.example.threedbe.bookmark.domain.Bookmark;
import com.example.threedbe.common.exception.ThreedConflictException;
import com.example.threedbe.post.domain.Post;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "members")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nickname;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String profileImageUrl;

	@Embedded
	private AuthProvider authProvider;

	@Embedded
	private RefreshToken refreshToken;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Bookmark> bookmarks;

	private LocalDateTime deletedAt;

	public boolean isDeleted() {
		return deletedAt != null;
	}

	public void addBookmark(Post post) {
		validateDuplicateBookmark(post);

		Bookmark bookmark = new Bookmark(this, post);
		this.bookmarks.add(bookmark);
		post.getBookmarks().add(bookmark);
	}

	private void validateDuplicateBookmark(Post post) {
		boolean isAlreadyBookmarked = this.bookmarks.stream()
			.anyMatch(bookmark -> bookmark.getPost().equals(post));
		if (isAlreadyBookmarked) {
			throw new ThreedConflictException("이미 북마크한 게시물입니다.");
		}
	}

}
