package com.few.api.web.controller.article

import com.few.api.domain.article.usecase.ReadArticleUseCase
import com.few.api.domain.article.usecase.BrowseArticlesUseCase
import com.few.api.domain.article.usecase.ReadArticleByEmailUseCase
import com.few.api.domain.article.usecase.dto.ReadArticleByEmailUseCaseIn
import com.few.api.domain.article.usecase.dto.ReadArticleUseCaseIn
import com.few.api.domain.article.usecase.dto.ReadArticlesUseCaseIn
import com.few.api.web.controller.article.request.ReadArticleByEmailRequest
import com.few.api.web.controller.article.response.ReadArticleResponse
import com.few.api.web.controller.article.response.ReadArticlesResponse
import com.few.api.web.controller.article.response.WorkbookInfo
import com.few.api.web.controller.article.response.WriterInfo
import com.few.api.web.support.ApiResponse
import com.few.api.web.support.ApiResponseGenerator
import com.few.api.web.support.EmailLogEventType
import com.few.api.web.support.SendType
import com.few.api.web.support.method.UserArgument
import com.few.api.web.support.method.UserArgumentDetails
import com.few.data.common.code.CategoryType
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping(value = ["/api/v1/articles"], produces = [MediaType.APPLICATION_JSON_VALUE])
class ArticleController(
    private val readArticleUseCase: ReadArticleUseCase,
    private val browseArticlesUseCase: BrowseArticlesUseCase,
    private val readArticleByEmailUseCase: ReadArticleByEmailUseCase,
) {

    @GetMapping("/{articleId}")
    fun readArticle(
        @UserArgument userArgumentDetails: UserArgumentDetails,
        @PathVariable(value = "articleId")
        @Min(value = 1, message = "{min.id}")
        articleId: Long,
    ): ApiResponse<ApiResponse.SuccessBody<ReadArticleResponse>> {
        val memberId = userArgumentDetails.id.toLong()

        val useCaseOut = ReadArticleUseCaseIn(
            articleId = articleId,
            memberId = memberId
        ).let { useCaseIn: ReadArticleUseCaseIn ->
            readArticleUseCase.execute(useCaseIn)
        }

        val response = ReadArticleResponse(
            id = useCaseOut.id,
            title = useCaseOut.title,
            writer = WriterInfo(
                id = useCaseOut.writer.id,
                name = useCaseOut.writer.name,
                url = useCaseOut.writer.url,
                imageUrl = useCaseOut.writer.imageUrl
            ),
            mainImageUrl = useCaseOut.mainImageUrl,
            content = useCaseOut.content,
            problemIds = useCaseOut.problemIds,
            category = useCaseOut.category,
            createdAt = useCaseOut.createdAt,
            views = useCaseOut.views
        )

        return ApiResponseGenerator.success(response, HttpStatus.OK)
    }

    @GetMapping
    fun readArticles(
        @RequestParam(
            required = false,
            defaultValue = "0"
        ) prevArticleId: Long,
        @RequestParam(
            required = false,
            defaultValue = "-1"
        ) categoryCd: Byte,
    ): ApiResponse<ApiResponse.SuccessBody<ReadArticlesResponse>> {
        val useCaseOut =
            browseArticlesUseCase.execute(ReadArticlesUseCaseIn(prevArticleId, categoryCd))

        val articles: List<ReadArticleResponse> = useCaseOut.articles.map { a ->
            ReadArticleResponse(
                id = a.id,
                title = a.title,
                writer = WriterInfo(
                    id = a.writer.id,
                    name = a.writer.name,
                    url = a.writer.url,
                    imageUrl = a.writer.imageUrl
                ),
                mainImageUrl = a.mainImageUrl,
                content = a.content,
                problemIds = a.problemIds,
                category = a.category,
                createdAt = a.createdAt,
                views = a.views,
                workbooks = a.workbooks.map { WorkbookInfo(it.id, it.title) }
            )
        }.toList()

        val response = ReadArticlesResponse(articles, useCaseOut.isLast)

        return ApiResponseGenerator.success(response, HttpStatus.OK)
    }

    @GetMapping("/categories")
    fun browseArticleCategories(): ApiResponse<ApiResponse.SuccessBody<Map<String, Any>>> {
        return ApiResponseGenerator.success(
            mapOf(
                "categories" to CategoryType.entries.map {
                    mapOf(
                        "code" to it.code,
                        "name" to it.displayName
                    )
                }
            ),
            HttpStatus.OK
        )
    }

    @PostMapping("/views")
    fun readArticleByEmail(
        @RequestParam("type") type: SendType,
        @RequestBody request: ReadArticleByEmailRequest,
    ): ApiResponse<ApiResponse.Success> {
        readArticleByEmailUseCase.execute(
            ReadArticleByEmailUseCaseIn(
                destination = request.destination,
                messageId = request.messageId,
                eventType = EmailLogEventType.OPEN,
                sendType = type
            )
        )
        return ApiResponseGenerator.success(HttpStatus.OK)
    }
}