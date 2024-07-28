package com.few.api.web.controller.member

import com.few.api.web.controller.member.request.SaveMemberRequest
import com.few.api.web.controller.member.response.SaveMemberResponse
import com.few.api.web.controller.member.response.TokenResponse
import com.few.api.web.support.ApiResponse
import com.few.api.web.support.ApiResponseGenerator
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping(value = ["/api/v1/members"], produces = [MediaType.APPLICATION_JSON_VALUE])
class MemberController {
    @PostMapping
    fun saveMember(
        @RequestBody request: SaveMemberRequest,
    ): ApiResponse<ApiResponse.SuccessBody<SaveMemberResponse>> {
        SaveMemberResponse(
            isSendAuth = true
        ).let {
            return ApiResponseGenerator.success(it, HttpStatus.OK)
        }
    }

    @PostMapping("/token")
    fun token(
        @RequestParam(value = "id", required = true) id: String,
        @RequestParam(value = "at", required = false) at: Long,
        @RequestParam(value = "rt", required = false) rt: Long,
    ): ApiResponse<ApiResponse.SuccessBody<TokenResponse>> {
        TokenResponse(
            accessToken = "accessToken",
            refreshToken = "refreshToken",
            isLogin = true
        ).let {
            return ApiResponseGenerator.success(it, HttpStatus.OK)
        }
    }
}