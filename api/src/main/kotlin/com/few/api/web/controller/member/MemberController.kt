package com.few.api.web.controller.member

import com.few.api.domain.member.usecase.DeleteMemberUseCase
import com.few.api.domain.member.usecase.SaveMemberUseCase
import com.few.api.domain.member.usecase.TokenUseCase
import com.few.api.domain.member.usecase.dto.DeleteMemberUseCaseIn
import com.few.api.domain.member.usecase.dto.SaveMemberUseCaseIn
import com.few.api.domain.member.usecase.dto.TokenUseCaseIn
import com.few.api.web.controller.member.request.SaveMemberRequest
import com.few.api.web.controller.member.request.TokenRequest
import com.few.api.web.controller.member.response.SaveMemberResponse
import com.few.api.web.controller.member.response.TokenResponse
import com.few.api.web.support.ApiResponse
import com.few.api.web.support.ApiResponseGenerator
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping(value = ["/api/v1/members"], produces = [MediaType.APPLICATION_JSON_VALUE])
class MemberController(
    private val saveMemberUseCase: SaveMemberUseCase,
    private val deleteMemberUseCase: DeleteMemberUseCase,
    private val tokenUseCase: TokenUseCase,
) {
    @PostMapping
    fun saveMember(
        @RequestBody request: SaveMemberRequest,
    ): ApiResponse<ApiResponse.SuccessBody<SaveMemberResponse>> {
        val useCaseOut = SaveMemberUseCaseIn(
            email = request.email
        ).let {
            saveMemberUseCase.execute(it)
        }

        SaveMemberResponse(
            isSendAuth = useCaseOut.isSendAuthEmail
        ).let {
            return ApiResponseGenerator.success(it, HttpStatus.OK)
        }
    }

    // todo add controller test after security is implemented
    @DeleteMapping()
    fun deleteMember(): ApiResponse<ApiResponse.Success> {
        val memberId = 1L // todo fix
        val useCaseOut = DeleteMemberUseCaseIn(
            memberId = memberId
        ).let {
            deleteMemberUseCase.execute(it)
        }

        return ApiResponseGenerator.success(HttpStatus.OK)
    }

    @PostMapping("/token")
    fun token(
        @RequestParam(value = "auth_token", required = false) token: String?,
        @RequestParam(value = "at", required = false) at: Long?,
        @RequestParam(value = "rt", required = false) rt: Long?,
        @RequestBody request: TokenRequest?,
    ): ApiResponse<ApiResponse.SuccessBody<TokenResponse>> {
        val useCaseOut = TokenUseCaseIn(
            token = token,
            at = at,
            rt = rt,
            refreshToken = request?.refreshToken
        ).let {
            tokenUseCase.execute(it)
        }

        TokenResponse(
            accessToken = useCaseOut.accessToken,
            refreshToken = useCaseOut.refreshToken,
            isLogin = useCaseOut.isLogin
        ).let {
            return ApiResponseGenerator.success(it, HttpStatus.OK)
        }
    }
}