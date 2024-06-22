package com.few.api.web.controller.article

import com.few.api.domain.article.dto.ReadArticleUseCaseIn
import com.few.api.domain.article.usecase.ReadArticleUseCase
import com.few.api.web.controller.article.response.ReadArticleResponse
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
@RequestMapping("/api/v1/articles")
class ArticleController(
    private val readArticleUseCase: ReadArticleUseCase
) {

    @GetMapping("/{articleId}")
    fun readArticle(
        @PathVariable(value = "articleId") articleId: Long
    ): ApiResponse<ApiResponse.SuccessBody<ReadArticleResponse>> {
        val useCaseOut = ReadArticleUseCaseIn(articleId).let { useCaseIn: ReadArticleUseCaseIn ->
            readArticleUseCase.execute(useCaseIn)
        }

        return ReadArticleResponse(useCaseOut).let {
            ApiResponseGenerator.success(it, HttpStatus.OK)
        }
    }
}