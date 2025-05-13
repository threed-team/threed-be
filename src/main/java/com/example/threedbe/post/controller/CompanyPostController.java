package com.example.threedbe.post.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.threedbe.common.dto.PageResponse;
import com.example.threedbe.post.dto.request.CompanyPostSearchRequest;
import com.example.threedbe.post.dto.response.CompanyPostDetailResponse;
import com.example.threedbe.post.dto.response.CompanyPostResponse;
import com.example.threedbe.post.service.CompanyPostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/company-posts")
@RequiredArgsConstructor
public class CompanyPostController implements CompanyPostControllerSwagger {

	private final CompanyPostService companyPostService;

	@Override
	@GetMapping("/search")
	public ResponseEntity<PageResponse<CompanyPostResponse>> search(
		@Valid CompanyPostSearchRequest companyPostSearchRequest) {

		PageResponse<CompanyPostResponse> search = companyPostService.search(companyPostSearchRequest);

		return ResponseEntity.ok(search);
	}

	@Override
	@GetMapping("/{postId}")
	public ResponseEntity<CompanyPostDetailResponse> getCompanyPostDetail(@PathVariable Long postId) {
		CompanyPostDetailResponse companyPostDetailResponse = companyPostService.getCompanyPostDetail(postId);

		return ResponseEntity.ok(companyPostDetailResponse);
	}

}
