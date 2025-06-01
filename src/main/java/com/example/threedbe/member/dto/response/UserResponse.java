package com.example.threedbe.member.dto.response;

import com.example.threedbe.member.domain.Member;

public record UserResponse(
	Long id,
	String email,
	String name,
	String profileImageUrl
) {
	public static UserResponse from(Member member) {
		return new UserResponse(
			member.getId(),
			member.getEmail(),
			member.getNickname(),
			member.getProfileImageUrl()
		);
	}
}
