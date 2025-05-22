package com.example.threedbe.crawler.dto;

import java.time.LocalDateTime;

/**
 * 크롤링된 콘텐츠 DTO
 */
public record CrawledContentDto(

	String title,

	String content,

	String url,

	String sourceName,

	String thumbnailImageUrl,

	LocalDateTime publishedAt

) {
}
