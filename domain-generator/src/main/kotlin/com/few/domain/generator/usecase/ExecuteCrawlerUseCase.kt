package com.few.domain.generator.usecase

import com.few.domain.generator.core.Extractor
import com.few.domain.generator.core.NaverNewsCrawler
import com.few.domain.generator.core.NewsModel
import com.few.domain.generator.usecase.dto.ExecuteCrawlerUseCaseIn
import com.few.domain.generator.usecase.dto.ExecuteCrawlerUseCaseOut
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import java.util.*

@Component
class ExecuteCrawlerUseCase(
    private val naverNewsCrawler: NaverNewsCrawler,
    private val extractor: Extractor,
) {
    private val log = KotlinLogging.logger {}

    //TODO: @Transactional
    fun execute(useCaseIn: ExecuteCrawlerUseCaseIn): ExecuteCrawlerUseCaseOut = runBlocking {
        /**
         * TODO: 아직 포스팅되지 않은 크롤링 데이터가 있는지 DB에서 확인
         * 있는 경우 조회해서 리턴
         * 없는 경우 크롤링 시작
         */

        //  1. 네이버 뉴스 크롤링
        log.info { "크롤링이 시작" }
        val newsUrls = naverNewsCrawler.getNaverNewsUrls(useCaseIn.sid)

        val results = mutableListOf<NewsModel>()
        for ((i, url) in newsUrls.withIndex()) {
            val newsData = naverNewsCrawler.getNewsContent(url)
            if (newsData != null) {
                results.add(newsData)
            }
            log.info { "뉴스 ${i + 1}/${newsUrls.size} 처리 완료" }
            Thread.sleep(1000) // 1초 딜레이
        }

        naverNewsCrawler.saveContentAsJson(results)
        log.info { "크롤링이 완료 및 요약 시작" }

        // 2. 뉴스 추출 및 요약
        extractor.extractAndSaveNews("crawled_news.json", "extracted_news.json")

        // 3. 뉴스 그룹화

        ExecuteCrawlerUseCaseOut(
            useCaseIn.sid,
            listOf(UUID.randomUUID().toString()) // TODO: DB 저장 시 크롤링 고유 ID 응답
        )
    }
}