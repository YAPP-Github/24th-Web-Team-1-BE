package com.few.api.web.controller.workbook

import com.few.api.domain.workbook.usecase.BrowseWorkbooksUseCase
import com.few.api.domain.workbook.usecase.dto.ReadWorkbookUseCaseIn
import com.few.api.domain.workbook.usecase.ReadWorkbookUseCase
import com.few.api.domain.workbook.usecase.dto.BrowseWorkbooksUseCaseIn
import com.few.api.security.filter.token.AccessTokenResolver
import com.few.api.security.token.TokenResolver
import com.few.api.web.controller.workbook.response.*
import com.few.api.web.support.WorkBookCategory
import com.few.api.web.support.ApiResponse
import com.few.api.web.support.ApiResponseGenerator
import com.few.api.web.support.ViewCategory
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping(value = ["/api/v1/workbooks"], produces = [MediaType.APPLICATION_JSON_VALUE])
class WorkBookController(
    private val readWorkbookUseCase: ReadWorkbookUseCase,
    private val browseWorkBooksUseCase: BrowseWorkbooksUseCase,
    private val tokenResolver: TokenResolver,
) {

    @GetMapping("/categories")
    fun browseWorkBookCategories(): ApiResponse<ApiResponse.SuccessBody<Map<String, Any>>> {
        return ApiResponseGenerator.success(
            mapOf(
                "categories" to WorkBookCategory.entries.map {
                    mapOf(
                        "code" to it.code,
                        "name" to it.displayName
                    )
                }
            ),
            HttpStatus.OK
        )
    }

    @GetMapping
    fun browseWorkBooks(
        servletRequest: HttpServletRequest,
        @RequestParam(value = "category", required = false)
        category: WorkBookCategory?,
        @RequestParam(value = "view", required = false)
        viewCategory: ViewCategory?,
    ): ApiResponse<ApiResponse.SuccessBody<BrowseWorkBooksResponse>> {
        val authorization: String? = servletRequest.getHeader("Authorization")
        val memberId = authorization?.let {
            AccessTokenResolver.resolve(it)
        }.let {
            tokenResolver.resolveId(it)
        }
        val useCaseOut =
            BrowseWorkbooksUseCaseIn(category ?: WorkBookCategory.All, viewCategory, memberId).let { useCaseIn ->
                browseWorkBooksUseCase.execute(useCaseIn)
            }

        BrowseWorkBooksResponse(
            useCaseOut.workbooks.map { workBookDetail ->
                BrowseWorkBookInfo(
                    workBookDetail.id,
                    workBookDetail.mainImageUrl,
                    workBookDetail.title,
                    workBookDetail.description,
                    workBookDetail.category,
                    workBookDetail.createdAt,
                    workBookDetail.writerDetails.map { writerDetail ->
                        WriterInfo(
                            writerDetail.id,
                            writerDetail.name,
                            writerDetail.url
                        )
                    },
                    workBookDetail.subscriptionCount
                )
            }
        ).let { response ->
            return ApiResponseGenerator.success(response, HttpStatus.OK)
        }
    }

    @GetMapping("/{workbookId}")
    fun readWorkBook(
        @PathVariable(value = "workbookId")
        @Min(value = 1, message = "{min.id}")
        workbookId: Long,
    ): ApiResponse<ApiResponse.SuccessBody<ReadWorkBookResponse>> {
        val useCaseOut = ReadWorkbookUseCaseIn(workbookId).let { useCaseIn ->
            readWorkbookUseCase.execute(useCaseIn)
        }

        ReadWorkBookResponse(useCaseOut).let { response ->
            return ApiResponseGenerator.success(response, HttpStatus.OK)
        }
    }
}