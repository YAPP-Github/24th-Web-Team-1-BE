package com.few.api.web.controller.hello

import com.few.api.web.controller.hello.request._HelloBody
import com.few.api.web.controller.hello.request._HelloParam
import com.few.api.web.support.ApiResponse
import com.few.api.web.support.ApiResponseGenerator
import com.few.api.web.support.MessageCode
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/api/v1/hello")
class _HelloController {

    /**
     * @param param 객체로 파라미터를 받는 경우
     * @param club RequestParam을 사용하는 경우
     */
    @GetMapping
    fun helloGet(
        param: _HelloParam?,
        @RequestParam(required = true) club: String
    ): ApiResponse<ApiResponse.SuccessBody<Map<String, String>>> {
        val name = param?.name ?: "few"
        val age = param?.age ?: 0
        val club = club
        val data =
            mapOf("hello" to "world", "name" to name, "age" to age.toString(), "club" to club)
        return ApiResponseGenerator.success(data, HttpStatus.OK)
    }

    @PostMapping
    fun helloPost(
        @RequestBody body: _HelloBody
    ): ApiResponse<ApiResponse.SuccessBody<Map<String, String>>> {
        val data = mapOf("hello" to "world", "name" to body.name)
        return ApiResponseGenerator.success(data, HttpStatus.OK, MessageCode.RESOURCE_CREATED)
    }

    @GetMapping("/error")
    fun helloError(): ApiResponse<ApiResponse.Success> {
        throw RuntimeException("Hello Error")
    }
}