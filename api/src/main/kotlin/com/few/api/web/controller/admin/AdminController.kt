package com.few.api.web.controller.admin

import com.few.api.domain.admin.document.dto.*
import com.few.api.domain.admin.document.usecase.AddArticleUseCase
import com.few.api.domain.admin.document.usecase.AddWorkbookUseCase
import com.few.api.domain.admin.document.usecase.ConvertContentUseCase
import com.few.api.domain.admin.document.usecase.MapArticleUseCase
import com.few.api.web.controller.admin.request.AddArticleRequest
import com.few.api.web.controller.admin.request.AddWorkbookRequest
import com.few.api.web.controller.admin.request.ConvertContentRequest
import com.few.api.web.controller.admin.request.MapArticleRequest
import com.few.api.web.controller.admin.response.AddArticleResponse
import com.few.api.web.controller.admin.response.AddWorkbookResponse
import com.few.api.web.controller.admin.response.ConvertContentResponse
import com.few.api.web.support.ApiResponse
import com.few.api.web.support.ApiResponseGenerator
import com.few.api.web.support.MessageCode
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping(value = ["/api/v1/admin"])
class AdminController(
    private val addArticleUseCase: AddArticleUseCase,
    private val addWorkbookUseCase: AddWorkbookUseCase,
    private val mapArticleUseCase: MapArticleUseCase,
    private val convertContentUseCase: ConvertContentUseCase
) {
    @PostMapping("/workbooks")
    fun addWorkbook(@RequestBody request: AddWorkbookRequest): ApiResponse<ApiResponse.SuccessBody<AddWorkbookResponse>> {
        val useCaseOut = AddWorkbookUseCaseIn(
            title = request.title,
            mainImageUrl = request.mainImageUrl,
            category = request.category,
            description = request.description
        ).let {
            addWorkbookUseCase.execute(it)
        }

        return AddWorkbookResponse(useCaseOut.workbookId).let {
            ApiResponseGenerator.success(it, HttpStatus.OK)
        }
    }

    @PostMapping("/articles")
    fun addArticle(
        @RequestBody request: AddArticleRequest
    ): ApiResponse<ApiResponse.SuccessBody<AddArticleResponse>> {
        val useCaseOut = AddArticleUseCaseIn(
            writerEmail = request.writerEmail,
            articleImageUrl = request.articleImageUrl,
            title = request.title,
            category = request.category,
            contentSource = request.contentSource,
            problemData = ProblemDetail(
                title = request.problemData.title,
                contents = request.problemData.contents.map {
                    ProblemContentDetail(
                        number = it.number,
                        content = it.content
                    )
                },
                answer = request.problemData.answer,
                explanation = request.problemData.explanation
            )
        ).let { useCaseIn ->
            addArticleUseCase.execute(useCaseIn)
        }

        return AddArticleResponse(useCaseOut).let {
            ApiResponseGenerator.success(it, HttpStatus.OK)
        }
    }

    @PostMapping("/relations/articles")
    fun mapArticle(@RequestBody request: MapArticleRequest): ApiResponse<ApiResponse.Success> {
        MapArticleUseCaseIn(
            workbookId = request.workbookId,
            articleId = request.articleId,
            dayCol = request.dayCol
        ).let {
            mapArticleUseCase.execute(it)
        }

        return ApiResponseGenerator.success(HttpStatus.OK, MessageCode.RESOURCE_CREATED)
    }

    @PostMapping("/utilities/conversion/content", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun convertContent(
        request: ConvertContentRequest
    ): ApiResponse<ApiResponse.SuccessBody<ConvertContentResponse>> {
        val useCaseOut = ConvertContentUseCaseIn(request.content).let {
            convertContentUseCase.execute(it)
        }

        ConvertContentResponse(useCaseOut.content, useCaseOut.originDownLoadUrl).let {
            return ApiResponseGenerator.success(it, HttpStatus.OK)
        }
    }
}