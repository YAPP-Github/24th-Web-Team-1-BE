package com.few.api.web.controller.workbook

import com.few.api.web.controller.workbook.request.SubWorkBookBody
import com.few.api.web.controller.workbook.response.ArticleInfo
import com.few.api.web.controller.workbook.response.ReadWorkBookResponse
import com.few.api.web.support.ApiResponse
import com.few.api.web.support.ApiResponseGenerator
import com.few.api.web.support.MessageCode
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@Validated
@RestController
@RequestMapping("/api/v1/workbooks")
class WorkBookController {

    @GetMapping("/{workbookId}")
    fun readWorkBook(
        @PathVariable(value = "workbookId") workbookId: Long
    ): ApiResponse<ApiResponse.SuccessBody<ReadWorkBookResponse>> {
        val data = ReadWorkBookResponse(
            id = 1L,
            name = 1L, // todo check
            mainImageUrl = "imageUrl",
            title = "title",
            description = "description",
            category = "category",
            createdAt = LocalDateTime.now(),
            writerIds = listOf(1L, 2L, 3L),
            articles = listOf(ArticleInfo(1L, "title1"), ArticleInfo(2L, "title2"))
        )
        return ApiResponseGenerator.success(data, HttpStatus.OK)
    }

    @PostMapping("/{workbookId}/subs")
    fun subWorkBook(@RequestBody body: SubWorkBookBody): ApiResponse<ApiResponse.Success> {
        return ApiResponseGenerator.success(HttpStatus.CREATED)
    }

    @DeleteMapping("/{workbookId}/subs")
    fun cancelSubWorkBook(): ApiResponse<ApiResponse.Success> {
        return ApiResponseGenerator.success(HttpStatus.OK, MessageCode.RESOURCE_DELETED)
    }
}