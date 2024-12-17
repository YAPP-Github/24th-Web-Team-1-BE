package com.few.api.domain.admin.controller

import com.few.api.domain.admin.controller.request.AddArticleRequest
import com.few.api.domain.admin.controller.request.AddWorkbookRequest
import com.few.api.domain.admin.controller.request.ConvertContentRequest
import com.few.api.domain.admin.controller.request.ImageSourceRequest
import com.few.api.domain.admin.controller.request.MapArticleRequest
import com.few.api.domain.admin.controller.response.AddArticleResponse
import com.few.api.domain.admin.controller.response.AddWorkbookResponse
import com.few.api.domain.admin.controller.response.ConvertContentResponse
import com.few.api.domain.admin.controller.response.ImageSourceResponse
import com.few.api.domain.admin.usecase.*
import com.few.api.domain.admin.usecase.dto.*
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import web.ApiResponse
import web.ApiResponseGenerator
import web.MessageCode

@Validated
@RestController
@RequestMapping(value = ["/api/v1/admin"])
class AdminController(
    private val addArticleUseCase: AddArticleUseCase,
    private val addWorkbookUseCase: AddWorkbookUseCase,
    private val mapArticleUseCase: MapArticleUseCase,
    private val convertContentUseCase: ConvertContentUseCase,
    private val putImageUseCase: PutImageUseCase,
) {
    @PostMapping("/workbooks")
    fun addWorkbook(
        @RequestBody request: AddWorkbookRequest,
    ): ApiResponse<ApiResponse.SuccessBody<AddWorkbookResponse>> {
        val useCaseOut =
            AddWorkbookUseCaseIn(
                title = request.title,
                mainImageUrl = request.mainImageUrl,
                category = request.category,
                description = request.description,
            ).let {
                addWorkbookUseCase.execute(it)
            }

        return AddWorkbookResponse(useCaseOut.workbookId).let {
            ApiResponseGenerator.success(it, HttpStatus.OK)
        }
    }

    @PostMapping("/articles")
    fun addArticle(
        @RequestBody request: AddArticleRequest,
    ): ApiResponse<ApiResponse.SuccessBody<AddArticleResponse>> {
        val useCaseOut =
            AddArticleUseCaseIn(
                writerEmail = request.writerEmail,
                articleImageUrl = request.articleImageUrl,
                title = request.title,
                category = request.category,
                contentType = request.contentType,
                contentSource = request.contentSource,
                problems =
                    request.problemData
                        .map { datum ->
                            ProblemDetail(
                                title = datum.title,
                                contents =
                                    datum.contents.map { detail ->
                                        ProblemContentDetail(
                                            number = detail.number,
                                            content = detail.content,
                                        )
                                    },
                                answer = datum.answer,
                                explanation = datum.explanation,
                            )
                        }.toList(),
            ).let { useCaseIn ->
                addArticleUseCase.execute(useCaseIn)
            }

        return AddArticleResponse(useCaseOut).let {
            ApiResponseGenerator.success(it, HttpStatus.OK)
        }
    }

    @PostMapping("/relations/articles")
    fun mapArticle(
        @RequestBody request: MapArticleRequest,
    ): ApiResponse<ApiResponse.Success> {
        MapArticleUseCaseIn(
            workbookId = request.workbookId,
            articleId = request.articleId,
            dayCol = request.dayCol,
        ).let {
            mapArticleUseCase.execute(it)
        }

        return ApiResponseGenerator.success(HttpStatus.OK, MessageCode.RESOURCE_CREATED)
    }

    @PostMapping("/utilities/conversion/content", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun convertContent(request: ConvertContentRequest): ApiResponse<ApiResponse.SuccessBody<ConvertContentResponse>> {
        val useCaseOut =
            ConvertContentUseCaseIn(request.content).let {
                convertContentUseCase.execute(it)
            }

        ConvertContentResponse(useCaseOut.content, useCaseOut.originDownLoadUrl).let {
            return ApiResponseGenerator.success(it, HttpStatus.OK)
        }
    }

    @PostMapping("/utilities/conversion/image", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun putImage(request: ImageSourceRequest): ApiResponse<ApiResponse.SuccessBody<ImageSourceResponse>> {
        val useCaseOut =
            PutImageUseCaseIn(request.source).let { useCaseIn: PutImageUseCaseIn ->
                putImageUseCase.execute(useCaseIn)
            }

        return ImageSourceResponse(useCaseOut.url, useCaseOut.supportSuffix).let {
            ApiResponseGenerator.success(it, HttpStatus.OK)
        }
    }
}