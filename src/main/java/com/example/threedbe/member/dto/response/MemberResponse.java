package com.example.threedbe.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MemberResponse(

	@Schema(description = "작성자 닉네임", example = "홍길동")
	String nickname,

	@Schema(description = "작성자 프로필 이미지 주소", example = "https://avatars.githubusercontent.com/u/210006781?s=48&v=4")
	String profileImageUrl

) {

	public static MemberResponse from(com.example.threedbe.member.domain.Member member) {

		return new MemberResponse(member.getNickname(), member.getProfileImageUrl());
	}

}
