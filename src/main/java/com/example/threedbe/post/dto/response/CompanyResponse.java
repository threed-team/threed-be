package com.example.threedbe.post.dto.response;

import com.example.threedbe.post.domain.Company;

import io.swagger.v3.oas.annotations.media.Schema;

public record CompanyResponse(

	@Schema(description = "소속 회사 이름", example = "네이버")
	String name,

	@Schema(description = "소속 회사 이미지 주소", example = "https://cdn.threed.site/company-logo/naver.ico")
	String logoImageUrl

) {

	public static CompanyResponse from(Company company) {

		return new CompanyResponse(company.getName(), company.getLogoImageUrl());
	}

}
