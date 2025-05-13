package com.example.threedbe.member.domain;

import java.time.LocalDateTime;
import java.util.Objects;

import com.example.threedbe.auth.domain.RefreshToken;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

	private LocalDateTime deletedAt;

	public boolean isDeleted() {
		return deletedAt != null;
	}

	@Override
	public final boolean equals(Object o) {
		if (!(o instanceof Member member))
			return false;

		return Objects.equals(id, member.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}
