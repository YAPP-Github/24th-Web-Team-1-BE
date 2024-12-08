package com.few.api.domain.log.controller

import com.few.api.domain.log.controller.request.EmailLogRequest
import com.few.api.domain.log.usecase.AddApiLogUseCase
import com.few.api.domain.log.usecase.AddEmailLogUseCase
import com.few.api.domain.log.dto.AddApiLogUseCaseIn
import com.few.api.domain.log.dto.AddEmailLogUseCaseIn
import com.few.api.domain.common.vo.EmailLogEventType
import com.few.api.domain.log.controller.request.ApiLogRequest
import web.ApiResponse
import web.ApiResponseGenerator
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
            eventType = EmailLogEventType.fromType(request.eventType)
                ?: throw IllegalArgumentException("EmailLogEventType not found. type=${request.eventType}"),
            messageId = request.messageId,
            destination = request.destination,
            mailTimestamp = request.mailTimestamp
        ).let {
            addEmailLogUseCase.execute(it)
        }
        return ApiResponseGenerator.success(HttpStatus.OK)
    }
}