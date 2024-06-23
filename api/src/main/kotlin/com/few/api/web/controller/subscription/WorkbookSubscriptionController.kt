package com.few.api.web.controller.subscription

import com.few.api.web.controller.subscription.request.SubscribeWorkbookRequest
import com.few.api.web.controller.subscription.request.UnsubscribeWorkbookRequest
import com.few.api.web.support.ApiResponse
import com.few.api.web.support.ApiResponseGenerator
import com.few.api.domain.subscription.SubscribeWorkbookUseCase
import com.few.api.domain.subscription.UnsubscribeWorkbookUseCase
import com.few.api.domain.subscription.`in`.SubscribeWorkbookUseCaseIn
import com.few.api.domain.subscription.`in`.UnsubscribeWorkbookUseCaseIn
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/api/v1/workbooks")
class WorkbookSubscriptionController(
    private val subscribeWorkbookUseCase: SubscribeWorkbookUseCase,
    private val unsubscribeWorkbookUseCase: UnsubscribeWorkbookUseCase
) {

    @PostMapping("{workbookId}/subs")
    fun subscribe(
        @PathVariable(value = "workbookId") workbookId: Long,
        @RequestBody body: SubscribeWorkbookRequest
    ): ApiResponse<ApiResponse.Success> {
        subscribeWorkbookUseCase.execute(
            SubscribeWorkbookUseCaseIn(workbookId = workbookId, email = body.email, memberId = 1L) // TODO: memberId
        )

        return ApiResponseGenerator.success(HttpStatus.OK)
    }

    @PostMapping("{workbookId}/unsubs")
    fun unsubscribe(
        @PathVariable(value = "workbookId") workbookId: Long,
        @RequestBody body: UnsubscribeWorkbookRequest
    ): ApiResponse<ApiResponse.Success> {
        unsubscribeWorkbookUseCase.execute(
            UnsubscribeWorkbookUseCaseIn(workbookId = workbookId, email = body.email, memberId = 1L, opinion = body.opinion) // TODO: memberId
        )

        return ApiResponseGenerator.success(HttpStatus.OK)
    }
}