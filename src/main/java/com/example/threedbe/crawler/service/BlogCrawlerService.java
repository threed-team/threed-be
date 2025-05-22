package com.example.threedbe.crawler.service;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.threedbe.common.dto.LlmResponseDto;
import com.example.threedbe.common.service.LlmService;
import com.example.threedbe.common.service.S3Service;
import com.example.threedbe.crawler.dto.BlogSource;
import com.example.threedbe.crawler.dto.CrawledContentDto;
import com.example.threedbe.post.domain.Company;
import com.example.threedbe.post.domain.CompanyPost;
import com.example.threedbe.post.domain.Field;
import com.example.threedbe.post.repository.CompanyPostRepository;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlogCrawlerService {

	private final CompanyPostRepository companyPostRepository;
	private final LlmService llmService;
	private final S3Service s3Service;

	@Transactional
	public int crawlAllSourcesAndCreatePosts() {
		int totalCreated = 0;

		for (BlogSource source : BlogSource.values()) {
			try {
				int created = crawlSourceAndCreatePosts(source);
				totalCreated += created;
				log.info("{}에서 {} 개의 새 포스트를 생성했습니다.", source.getDisplayName(), created);
			} catch (Exception e) {
				log.error("{}에서 크롤링 및 포스트 생성 중 오류 발생: {}", source.getDisplayName(), e.getMessage(), e);
			}
		}

		return totalCreated;
	}

	private int crawlSourceAndCreatePosts(BlogSource source) {
		int createdCount = 0;

		List<CrawledContentDto> contents = new ArrayList<>();

		switch (source) {
			case NAVER -> contents = crawlNaver();
			default -> log.warn("지원하지 않는 블로그 소스: {}", source.getDisplayName());
		}

		for (CrawledContentDto dto : contents) {
			if (createCompanyPost(dto)) {
				createdCount++;
			}
		}

		return createdCount;
	}

	private List<CrawledContentDto> crawlNaver() {
		log.info("네이버 블로그 크롤링 시작 (RSS 피드 사용)");
		List<CrawledContentDto> results = new ArrayList<>();

		try {
			URL feedUrl = new URL(BlogSource.NAVER.getBaseUrl());
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(feedUrl));

			for (SyndEntry entry : feed.getEntries()) {
				String title = entry.getTitle();
				String postUrl = entry.getLink();

				if (companyPostRepository.existsBySourceUrl(postUrl)) {
					continue;
				}

				LocalDateTime publishedAt = entry.getUpdatedDate()
					.toInstant()
					.atZone(ZoneId.systemDefault())
					.toLocalDateTime();

				StringBuilder urlBuilder = new StringBuilder().append(feedUrl.getProtocol())
					.append("://")
					.append(feedUrl.getHost());
				for (SyndContent content : entry.getContents()) {
					Pattern pattern = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
					Matcher matcher = pattern.matcher(content.getValue());
					if (matcher.find()) {
						urlBuilder.append(matcher.group(1));
						break;
					}
				}

				String contentText = extractTextFromSyndContent(entry.getContents());

				CrawledContentDto dto = new CrawledContentDto(
					title,
					contentText,
					postUrl,
					BlogSource.NAVER.getDisplayName(),
					urlBuilder.toString(),
					publishedAt
				);

				results.add(dto);
			}
		} catch (Exception e) {
			log.error("네이버 RSS 피드 파싱 중 오류 발생: {}", e.getMessage(), e);
		}

		return results;
	}

	public boolean createCompanyPost(CrawledContentDto dto) {
		LlmResponseDto llmResponse = summarizeContent(dto);

		BufferedImage bufferedImage = processThumbnailImage(dto.thumbnailImageUrl());
		String title = dto.title();
		String thumbnailUrl = s3Service.uploadThumbnailImage(bufferedImage, title);

		Field field = determineField(llmResponse.field());

		Company company = determineCompany(dto.sourceName());

		CompanyPost companyPost = new CompanyPost(
			title,
			llmResponse.summary(),
			thumbnailUrl,
			field,
			dto.publishedAt(),
			company,
			dto.url()
		);

		companyPostRepository.save(companyPost);
		log.info("새 회사 포스트 생성: {}, 회사: {}, 분야: {}", title, company, field);

		return true;
	}

	private LlmResponseDto summarizeContent(CrawledContentDto dto) {

		return llmService.generate(dto.content());
	}

	private BufferedImage processThumbnailImage(String thumbnailUrl) {
		try {
			URL imageUrl = new URL(thumbnailUrl);

			return ImageIO.read(imageUrl);
		} catch (Exception e) {
			log.error("썸네일 이미지 다운로드 및 업로드 중 오류 발생: {}", e.getMessage());
			return null;
		}
	}

	private Field determineField(String fieldStr) {
		return switch (fieldStr.toUpperCase()) {
			case "AI" -> Field.AI;
			case "BACKEND" -> Field.BACKEND;
			case "FRONTEND" -> Field.FRONTEND;
			case "DEVOPS" -> Field.DEVOPS;
			case "MOBILE" -> Field.MOBILE;
			case "DB" -> Field.DB;
			case "COLLAB TOOL" -> Field.COLLAB_TOOL;
			default -> Field.ETC;
		};
	}

	private Company determineCompany(String sourceName) {
		return switch (sourceName) {
			case "네이버" -> Company.NAVER;
			case "카카오" -> Company.KAKAO;
			case "라인" -> Company.LINE;
			case "토스" -> Company.TOSS;
			default -> Company.ETC;
		};
	}

	private String extractTextFromSyndContent(List<SyndContent> contents) {
		StringBuilder contentBuilder = new StringBuilder();
		for (SyndContent content : contents) {
			String htmlContent = content.getValue();
			if (htmlContent != null && !htmlContent.isBlank()) {
				String plainText = htmlContent.replaceAll("<[^>]*>", " ")
					.replaceAll("\\s+", " ")
					.trim();
				contentBuilder.append(plainText);
			}
		}

		return contentBuilder.toString();
	}
}
