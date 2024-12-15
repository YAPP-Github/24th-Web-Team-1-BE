package com.few.domain.generator.controller

import com.few.domain.generator.controller.response.ExecuteCrawlerResponse
import com.few.domain.generator.usecase.ExecuteCrawlerUseCase
import com.few.domain.generator.usecase.dto.ExecuteCrawlerUseCaseIn
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import web.ApiResponse
import web.ApiResponseGenerator

@Validated
@RestController
@RequestMapping(value = ["/api/v2/crawlers"], produces = [MediaType.APPLICATION_JSON_VALUE])
class CrawlerController(
    private val executeCrawlerUseCase: ExecuteCrawlerUseCase,
) {

    /**
     * 아직 포스팅 되지 않은 크롤링 데이터 조회
     * 만약 크롤링하고 포스팅되지 않은 데이터가 있을 경우
     * 해당 데이터의 식별자들을 응답하고 포스팅되지 않은 데이터가 없을 경우
     * 크롤링을 수행함
     */
    @GetMapping
    fun executeCrawler(
        /**
         * 100(정치), 10(경제), 105(IT/과학)
         */
        @RequestParam(
            required = false,
            defaultValue = "0"
        ) sid: Int,
    ): ApiResponse<ApiResponse.SuccessBody<ExecuteCrawlerResponse>> {
        val useCaseOut = executeCrawlerUseCase.execute(ExecuteCrawlerUseCaseIn(sid))

        return ApiResponseGenerator.success(
            ExecuteCrawlerResponse(useCaseOut.sid, useCaseOut.crawlingIds),
            HttpStatus.OK
        )
    }
}