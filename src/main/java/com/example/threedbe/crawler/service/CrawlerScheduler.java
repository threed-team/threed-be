package com.example.threedbe.crawler.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrawlerScheduler {

	private final BlogCrawlerService blogCrawlerService;

	@Scheduled(cron = "0 0 2 * * ?")
	public void scheduledCrawlingAndPosting() {
		log.info("스케줄링된 기술 블로그 크롤링 및 포스트 생성 시작");
		try {
			int crawledCount = blogCrawlerService.crawlAllSourcesAndCreatePosts();
			log.info("스케줄링된 크롤링 및 포스트 생성 완료: {} 개의 새 포스트를 생성했습니다.", crawledCount);
		} catch (Exception e) {
			log.error("스케줄링된 크롤링 및 포스트 생성 중 오류 발생: {}", e.getMessage(), e);
		}
	}

}
