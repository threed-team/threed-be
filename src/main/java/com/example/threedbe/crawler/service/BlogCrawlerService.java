package com.example.threedbe.crawler.service;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.threedbe.crawler.dto.BlogSource;
import com.example.threedbe.crawler.dto.CrawledContentDto;
import com.example.threedbe.post.service.CompanyPostService;
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

	private final CompanyPostService companyPostService;

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
			case KAKAO -> contents = crawlKakao();
			default -> log.warn("지원하지 않는 블로그 소스: {}", source.getDisplayName());
		}

		for (CrawledContentDto dto : contents) {
			if (companyPostService.createCompanyPost(dto)) {
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

				if (companyPostService.existsBySourceUrl(postUrl)) {
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

	private List<CrawledContentDto> crawlKakao() {
		log.info("카카오 블로그 크롤링 시작 (RSS 피드 사용)");
		List<CrawledContentDto> results = new ArrayList<>();

		try {
			URL feedUrl = new URL(BlogSource.KAKAO.getBaseUrl());
			SAXBuilder saxBuilder = new SAXBuilder();
			Document document = saxBuilder.build(feedUrl);
			Element rootElement = document.getRootElement();
			Element channel = rootElement.getChild("channel");
			List<Element> items = channel.getChildren("item");

			for (Element item : items) {
				String title = item.getChildText("title");
				if (title != null && title.startsWith("<![CDATA[") && title.endsWith("]]>")) {
					title = title.substring(9, title.length() - 3);
				}

				String postUrl = item.getChildText("link");

				if (companyPostService.existsBySourceUrl(postUrl)) {
					continue;
				}

				String pubDateStr = item.getChildText("pubDate");
				SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
				LocalDateTime publishedAt = dateFormat.parse(pubDateStr)
					.toInstant()
					.atZone(ZoneId.systemDefault())
					.toLocalDateTime();

				String thumbnailUrl = item.getChildText("thumbnail");

				String contentText = "";
				ChromeOptions options = new ChromeOptions();
				options.addArguments("--headless");
				options.addArguments("--disable-gpu");
				options.addArguments("--window-size=1920,1080");
				options.addArguments("--disable-extensions");
				options.addArguments("--no-sandbox");
				options.addArguments("--disable-dev-shm-usage");

				WebDriver driver = new ChromeDriver(options);
				try {
					driver.get(postUrl);

					WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
					wait.until(JavascriptExecutor.class::cast)
						.executeScript("return document.readyState").equals("complete");

					String selector = "div.daum-wm-content.preview";
					List<WebElement> elements = driver.findElements(
						By.cssSelector(selector));

					if (!elements.isEmpty()) {
						String text = elements.get(0).getText().trim();
						if (!text.isEmpty()) {
							contentText = text.replaceAll("\\r?\\n", " ");
						}
					}

				} finally {
					driver.quit();
					log.info("WebDriver 종료 완료");
				}

				CrawledContentDto dto = new CrawledContentDto(
					title,
					contentText,
					postUrl,
					BlogSource.KAKAO.getDisplayName(),
					thumbnailUrl,
					publishedAt
				);

				results.add(dto);
			}
		} catch (Exception e) {
			log.error("카카오 RSS 피드 파싱 중 오류 발생: {}", e.getMessage(), e);
		}

		return results;
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
