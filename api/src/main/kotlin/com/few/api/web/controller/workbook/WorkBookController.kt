package com.few.api.web.controller.workbook

import com.few.api.web.controller.workbook.response.ArticleInfo
import com.few.api.web.controller.workbook.response.ReadWorkBookResponse
import com.few.api.web.controller.workbook.response.WriterInfo
import com.few.api.web.support.ApiResponse
import com.few.api.web.support.ApiResponseGenerator
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URL
import java.time.LocalDateTime

@Validated
@RestController
@RequestMapping(value = ["/api/v1/workbooks"], produces = [MediaType.APPLICATION_JSON_VALUE])
class WorkBookController {

    @GetMapping("/{workbookId}")
    fun readWorkBook(
        @PathVariable(value = "workbookId")
        @Min(1)
        workbookId: Long
    ): ApiResponse<ApiResponse.SuccessBody<ReadWorkBookResponse>> {
        val data = ReadWorkBookResponse(
            id = 1L,
            mainImageUrl = "/main_img.png",
            title = "재태크, 투자 필수 용어 모음집",
            description = "사회 초년생부터, 직장인, 은퇴자까지 모두가 알아야 할 기본적인 재태크, 투자 필수 용어 모음집 입니다.",
            category = "경제",
            createdAt = LocalDateTime.now(),
            writers = listOf(
                WriterInfo(1L, "안나포", URL("http://localhost:8080/api/v1/users/1")),
                WriterInfo(2L, "퓨퓨", URL("http://localhost:8080/api/v1/users/2")),
                WriterInfo(3L, "프레소", URL("http://localhost:8080/api/v1/users/3"))
            ),
            articles = listOf(ArticleInfo(1L, "ISA(개인종합자산관리계좌)란?"), ArticleInfo(2L, "ISA(개인종합자산관리계좌)란? ISA(개인종합자산관리계좌)란? ISA(개인종합자산관리계좌)란? ISA(개인종합자산관리계좌)란? ISA(개인종합자산관리계좌)란? ISA(개인종합자산관리계좌)란?"))
        )
        return ApiResponseGenerator.success(data, HttpStatus.OK)
    }
}