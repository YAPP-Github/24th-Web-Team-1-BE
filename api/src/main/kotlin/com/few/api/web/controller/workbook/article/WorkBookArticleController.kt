package com.few.api.web.controller.workbook.article

import com.few.api.domain.workbook.article.dto.ReadWorkBookArticleUseCaseIn
import com.few.api.domain.workbook.article.usecase.ReadWorkBookArticleUseCase
import com.few.api.web.controller.workbook.article.response.ReadWorkBookArticleResponse
import com.few.api.web.support.ApiResponse
import com.few.api.web.support.ApiResponseGenerator
import com.few.api.web.support.method.UserArgument
import com.few.api.web.support.method.UserArgumentDetails
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping(value = ["/api/v1/workbooks/{workbookId}/articles"], produces = [MediaType.APPLICATION_JSON_VALUE])
class WorkBookArticleController(
    private val readWorkBookArticleUseCase: ReadWorkBookArticleUseCase,
) {

    @GetMapping("/{articleId}")
    fun readWorkBookArticle(
        @UserArgument userArgumentDetails: UserArgumentDetails,
        @PathVariable(value = "workbookId")
        @Min(value = 1, message = "{min.id}")
        workbookId: Long,
        @PathVariable(value = "articleId")
        @Min(value = 1, message = "{min.id}")
        articleId: Long,
    ): ApiResponse<ApiResponse.SuccessBody<ReadWorkBookArticleResponse>> {
        val memberId = userArgumentDetails.id.toLong()

        val useCaseOut = ReadWorkBookArticleUseCaseIn(
            workbookId = workbookId,
            articleId = articleId,
            memberId
        ).let { useCaseIn: ReadWorkBookArticleUseCaseIn ->
            readWorkBookArticleUseCase.execute(useCaseIn)
        }

        return ReadWorkBookArticleResponse(useCaseOut).let {
            ApiResponseGenerator.success(it, HttpStatus.OK)
        }
    }
}