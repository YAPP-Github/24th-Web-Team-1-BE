package com.few.api.web.controller.workbook

import com.few.api.domain.workbook.usecase.dto.ReadWorkbookUseCaseIn
import com.few.api.domain.workbook.usecase.ReadWorkbookUseCase
import com.few.api.web.support.WorkBookCategory
import com.few.api.web.controller.workbook.response.BrowseWorkBookInfo
import com.few.api.web.controller.workbook.response.BrowseWorkBooksResponse
import com.few.api.web.controller.workbook.response.ReadWorkBookResponse
import com.few.api.web.controller.workbook.response.WriterInfo
import com.few.api.web.support.ApiResponse
import com.few.api.web.support.ApiResponseGenerator
import com.few.data.common.code.CategoryType
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URL
import java.time.LocalDateTime

@Validated
@RestController
@RequestMapping(value = ["/api/v1/workbooks"], produces = [MediaType.APPLICATION_JSON_VALUE])
class WorkBookController(
    private val readWorkbookUseCase: ReadWorkbookUseCase,
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
        @RequestParam(value = "category", required = false)
        category: WorkBookCategory?,
    ): ApiResponse<ApiResponse.SuccessBody<BrowseWorkBooksResponse>> {
        BrowseWorkBooksResponse(
            listOf(
                BrowseWorkBookInfo(
                    id = 1,
                    mainImageUrl = URL("https://example.com"),
                    title = "title1",
                    description = "description1",
                    category = CategoryType.ECONOMY.displayName,
                    createdAt = LocalDateTime.now(),
                    writers = listOf(
                        WriterInfo(
                            id = 1,
                            name = "name1",
                            url = URL("https://example.com")
                        )
                    ),
                    subscriberCount = 1
                ),
                BrowseWorkBookInfo(
                    id = 2,
                    mainImageUrl = URL("https://example.com"),
                    title = "title2",
                    description = "description2",
                    category = CategoryType.ECONOMY.displayName,
                    createdAt = LocalDateTime.now(),
                    writers = listOf(
                        WriterInfo(
                            id = 2,
                            name = "name2",
                            url = URL("https://example.com")
                        )
                    ),
                    subscriberCount = 2
                )
            )
        ).let {
            return ApiResponseGenerator.success(it, HttpStatus.OK)
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