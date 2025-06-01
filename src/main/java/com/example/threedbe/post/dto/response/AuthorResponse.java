package com.example.threedbe.post.dto.response;

import com.example.threedbe.member.domain.Member;
import com.example.threedbe.post.domain.Company;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthorResponse(

	@Schema(description = "저자 이름 (회사 or 회원)", example = "네이버")
	String name,

	@Schema(description = "저자 이미지 주소", example = "https://avatars.githubusercontent.com/u/210006781?s=48&v=4")
	String imageUrl

) {

	public static AuthorResponse from(Company company) {

		return new AuthorResponse(company.getName(), company.getLogoImageUrl());
	}

	public static AuthorResponse from(Member member) {

		return new AuthorResponse(member.getNickname(), member.getProfileImageUrl());
	}

}
