package com.few.api.web.controller.admin

import com.few.api.domain.log.AddApiLogUseCase
import com.few.api.domain.log.AddEmailLogUseCase
import com.few.api.domain.log.dto.AddApiLogUseCaseIn
import com.few.api.domain.log.dto.AddEmailLogUseCaseIn
import com.few.api.web.controller.admin.request.*
import com.few.api.web.support.ApiResponse
import com.few.api.web.support.ApiResponseGenerator
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping(value = ["/api/v1/logs"])
class ApiLogController(
    private val addApiLogUseCase: AddApiLogUseCase,
    private val addEmailLogUseCase: AddEmailLogUseCase,
) {
    @PostMapping
    fun addApiLog(@RequestBody request: ApiLogRequest): ApiResponse<ApiResponse.Success> {
        AddApiLogUseCaseIn(request.history).let {
            addApiLogUseCase.execute(it)
        }

        return ApiResponseGenerator.success(HttpStatus.OK)
    }

    @PostMapping("/email/articles")
    fun addEmailLog(@RequestBody request: EmailLogRequest): ApiResponse<ApiResponse.Success> {
        AddEmailLogUseCaseIn(
            eventType = request.eventType,
            messageId = request.messageId,
            destination = request.destination,
            mailTimestamp = request.mailTimestamp
        ).let {
            addEmailLogUseCase.execute(it)
        }
        return ApiResponseGenerator.success(HttpStatus.OK)
    }
}