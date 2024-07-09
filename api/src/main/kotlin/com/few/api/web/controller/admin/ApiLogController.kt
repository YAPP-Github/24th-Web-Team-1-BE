package com.few.api.web.controller.admin

import com.few.api.domain.log.AddApiLogUseCase
import com.few.api.domain.log.dto.AddApiLogUseCaseIn
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
    private val addApiLogUseCase: AddApiLogUseCase
) {
    @PostMapping
    fun addApiLog(@RequestBody request: ApiLogRequest): ApiResponse<ApiResponse.Success> {
        AddApiLogUseCaseIn(request.history).let {
            addApiLogUseCase.execute(it)
        }

        return ApiResponseGenerator.success(HttpStatus.OK)
    }
}