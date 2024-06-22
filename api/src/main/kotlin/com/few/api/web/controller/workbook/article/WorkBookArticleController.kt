package com.few.api.web.controller.workbook.article

import com.few.api.domain.workbook.article.dto.ReadWorkBookArticleUseCaseIn
import com.few.api.domain.workbook.article.usecase.ReadWorkBookArticleUseCase
import com.few.api.web.controller.workbook.article.response.ReadWorkBookArticleResponse
import com.few.api.web.support.ApiResponse
import com.few.api.web.support.ApiResponseGenerator
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/api/v1/workbooks/{workbookId}/articles")
class WorkBookArticleController(
    private val readWorkBookArticleUseCase: ReadWorkBookArticleUseCase
) {

    @GetMapping("/{articleId}")
    fun readWorkBookArticle(
        @PathVariable(value = "workbookId") workbookId: Long,
        @PathVariable(value = "articleId") articleId: Long
    ): ApiResponse<ApiResponse.SuccessBody<ReadWorkBookArticleResponse>> {
        val useCaseOut = ReadWorkBookArticleUseCaseIn(workbookId = workbookId, articleId = articleId).let { useCaseIn: ReadWorkBookArticleUseCaseIn ->
            readWorkBookArticleUseCase.execute(useCaseIn)
        }

        return ReadWorkBookArticleResponse(useCaseOut).let {
            ApiResponseGenerator.success(it, HttpStatus.OK)
        }
    }
}