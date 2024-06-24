package com.few.api.web.controller.workbook

import com.few.api.domain.workbook.dto.ReadWorkbookUseCaseIn
import com.few.api.domain.workbook.usecase.ReadWorkbookUseCase
import com.few.api.web.controller.workbook.response.ReadWorkBookResponse
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
@RequestMapping("/api/v1/workbooks")
class WorkBookController(
    private val readWorkbookUseCase: ReadWorkbookUseCase
) {

    @GetMapping("/{workbookId}")
    fun readWorkBook(
        @PathVariable(value = "workbookId") workbookId: Long
    ): ApiResponse<ApiResponse.SuccessBody<ReadWorkBookResponse>> {
        val useCaseOut = ReadWorkbookUseCaseIn(workbookId).let { useCaseIn ->
            readWorkbookUseCase.execute(useCaseIn)
        }

        ReadWorkBookResponse(useCaseOut).let { response ->
            return ApiResponseGenerator.success(response, HttpStatus.OK)
        }
    }
}