package com.few.api.web.controller.subscription

import com.few.api.domain.subscription.usecase.BrowseSubscribeWorkbooksUseCase
import com.few.api.web.controller.subscription.request.SubscribeWorkbookRequest
import com.few.api.web.controller.subscription.request.UnsubscribeWorkbookRequest
import com.few.api.web.support.ApiResponse
import com.few.api.web.support.ApiResponseGenerator
import com.few.api.domain.subscription.usecase.SubscribeWorkbookUseCase
import com.few.api.domain.subscription.usecase.UnsubscribeAllUseCase
import com.few.api.domain.subscription.usecase.UnsubscribeWorkbookUseCase
import com.few.api.domain.subscription.usecase.dto.BrowseSubscribeWorkbooksUseCaseIn
import com.few.api.domain.subscription.usecase.dto.SubscribeWorkbookUseCaseIn
import com.few.api.domain.subscription.usecase.dto.UnsubscribeAllUseCaseIn
import com.few.api.domain.subscription.usecase.dto.UnsubscribeWorkbookUseCaseIn
import com.few.api.web.controller.subscription.request.UnsubscribeAllRequest
import com.few.api.web.controller.subscription.response.SubscribeWorkbookInfo
import com.few.api.web.controller.subscription.response.SubscribeWorkbooksResponse
import com.few.api.web.support.ViewCategory
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping(value = ["/api/v1"], produces = [MediaType.APPLICATION_JSON_VALUE])
class SubscriptionController(
    private val subscribeWorkbookUseCase: SubscribeWorkbookUseCase,
    private val unsubscribeWorkbookUseCase: UnsubscribeWorkbookUseCase,
    private val unsubscribeAllUseCase: UnsubscribeAllUseCase,
    private val browseSubscribeWorkbooksUseCase: BrowseSubscribeWorkbooksUseCase,
) {

    // todo add auth
    @GetMapping("/subscriptions/workbooks")
    fun browseSubscribeWorkbooks(
        @RequestParam(
            value = "view",
            required = false
        ) view: ViewCategory? = ViewCategory.MAIN_CARD,
    ): ApiResponse<ApiResponse.SuccessBody<SubscribeWorkbooksResponse>> {
        // todo fix memberId
        val memberId = 1L
        val useCaseOut = BrowseSubscribeWorkbooksUseCaseIn(memberId).let {
            browseSubscribeWorkbooksUseCase.execute(it)
        }

        return SubscribeWorkbooksResponse(
            workbooks = useCaseOut.workbooks.map {
                SubscribeWorkbookInfo(
                    id = it.workbookId,
                    currentDay = it.currentDay,
                    totalDay = it.totalDay,
                    status = it.isActiveSub.name,
                    rank = it.rank,
                    totalSubscriber = it.totalSubscriber,
                    articleInfo = it.articleInfo
                )
            }
        ).let {
            ApiResponseGenerator.success(it, HttpStatus.OK)
        }
    }

    @PostMapping("/workbooks/{workbookId}/subs")
    fun subscribeWorkbook(
        @PathVariable(value = "workbookId")
        @Min(value = 1, message = "{min.id}")
        workbookId: Long,
        @Valid @RequestBody
        body: SubscribeWorkbookRequest,
    ): ApiResponse<ApiResponse.Success> {
        subscribeWorkbookUseCase.execute(
            SubscribeWorkbookUseCaseIn(workbookId = workbookId, email = body.email)
        )

        return ApiResponseGenerator.success(HttpStatus.OK)
    }

    @PostMapping("/workbooks/{workbookId}/unsubs")
    fun unsubscribeWorkbook(
        @PathVariable(value = "workbookId")
        @Min(value = 1, message = "{min.id}")
        workbookId: Long,
        @Valid @RequestBody
        body: UnsubscribeWorkbookRequest,
    ): ApiResponse<ApiResponse.Success> {
        unsubscribeWorkbookUseCase.execute(
            UnsubscribeWorkbookUseCaseIn(workbookId = workbookId, email = body.email, opinion = body.opinion)
        )

        return ApiResponseGenerator.success(HttpStatus.OK)
    }

    @PostMapping("/subscriptions/unsubs")
    fun deactivateAllSubscriptions(
        @Valid @RequestBody
        body: UnsubscribeAllRequest,
    ): ApiResponse<ApiResponse.Success> {
        unsubscribeAllUseCase.execute(
            UnsubscribeAllUseCaseIn(email = body.email, opinion = body.opinion)
        )

        return ApiResponseGenerator.success(HttpStatus.OK)
    }
}