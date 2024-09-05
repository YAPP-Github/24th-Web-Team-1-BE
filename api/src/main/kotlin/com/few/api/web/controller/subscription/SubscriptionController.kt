package com.few.api.web.controller.subscription

import com.few.api.domain.subscription.usecase.*
import com.few.api.web.controller.subscription.request.UnsubscribeWorkbookRequest
import com.few.api.web.support.ApiResponse
import com.few.api.web.support.ApiResponseGenerator
import com.few.api.domain.subscription.usecase.dto.*
import com.few.api.security.authentication.token.TokenUserDetails
import com.few.api.web.controller.subscription.request.UnsubscribeAllRequest
import com.few.api.web.controller.subscription.request.UpdateSubscriptionDayRequest
import com.few.api.web.controller.subscription.request.UpdateSubscriptionTimeRequest
import com.few.api.web.controller.subscription.response.*
import com.few.api.web.support.DayCode
import com.few.api.web.support.ViewCategory
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.lang.IllegalStateException

@Validated
@RestController
@RequestMapping(value = ["/api/v1"], produces = [MediaType.APPLICATION_JSON_VALUE])
class SubscriptionController(
    private val subscribeWorkbookUseCase: SubscribeWorkbookUseCase,
    private val unsubscribeWorkbookUseCase: UnsubscribeWorkbookUseCase,
    private val unsubscribeAllUseCase: UnsubscribeAllUseCase,
    private val browseSubscribeWorkbooksUseCase: BrowseSubscribeWorkbooksUseCase,
    private val updateSubscriptionDayUseCase: UpdateSubscriptionDayUseCase,
    private val updateSubscriptionTimeUseCase: UpdateSubscriptionTimeUseCase,
) {

    @GetMapping("/subscriptions/workbooks")
    fun browseSubscribeWorkbooks(
        @AuthenticationPrincipal userDetails: TokenUserDetails,
        @RequestParam(
            value = "view",
            required = false
        ) view: ViewCategory?,
    ): ApiResponse<ApiResponse.SuccessBody<SubscribeWorkbooksResponse>> {
        val memberId = userDetails.username.toLong()
        val useCaseOut =
            BrowseSubscribeWorkbooksUseCaseIn(memberId, view ?: ViewCategory.MAIN_CARD).let {
                browseSubscribeWorkbooksUseCase.execute(it)
            }

        // todo fix to facade usecase
        return SubscribeWorkbooksResponse(
            workbooks = when (useCaseOut.clazz) {
                MainCardSubscribeWorkbookDetail::class.java -> useCaseOut.workbooks.map { it as MainCardSubscribeWorkbookDetail }.map {
                    MainCardSubscribeWorkbookInfo(
                        id = it.workbookId,
                        status = it.isActiveSub.name,
                        totalDay = it.totalDay,
                        currentDay = it.currentDay,
                        rank = it.rank,
                        totalSubscriber = it.totalSubscriber,
                        subscription = it.subscription,
                        articleInfo = it.articleInfo
                    )
                }
                MyPageSubscribeWorkbookDetail::class.java -> useCaseOut.workbooks.map { it as MyPageSubscribeWorkbookDetail }.map {
                    MyPageSubscribeWorkbookInfo(
                        id = it.workbookId,
                        status = it.isActiveSub.name,
                        totalDay = it.totalDay,
                        currentDay = it.currentDay,
                        rank = it.rank,
                        totalSubscriber = it.totalSubscriber,
                        subscription = it.subscription,
                        workbookInfo = it.workbookInfo
                    )
                }
                else -> throw IllegalStateException("Invalid class type")
            }
        ).let {
            ApiResponseGenerator.success(it, HttpStatus.OK)
        }
    }

    // todo fix email to memberId
    @PostMapping("/workbooks/{workbookId}/subs")
    fun subscribeWorkbook(
        @AuthenticationPrincipal userDetails: TokenUserDetails,
        @PathVariable(value = "workbookId")
        @Min(value = 1, message = "{min.id}")
        workbookId: Long,
    ): ApiResponse<ApiResponse.Success> {
        val memberId = userDetails.username.toLong()
        subscribeWorkbookUseCase.execute(
            SubscribeWorkbookUseCaseIn(workbookId = workbookId, memberId = memberId)
        )

        return ApiResponseGenerator.success(HttpStatus.OK)
    }

    // todo fix email to memberId
    @PostMapping("/workbooks/{workbookId}/unsubs")
    fun unsubscribeWorkbook(
        @AuthenticationPrincipal userDetails: TokenUserDetails,
        @PathVariable(value = "workbookId")
        @Min(value = 1, message = "{min.id}")
        workbookId: Long,
        @Valid @RequestBody
        body: UnsubscribeWorkbookRequest,
    ): ApiResponse<ApiResponse.Success> {
        val memberId = userDetails.username.toLong()
        unsubscribeWorkbookUseCase.execute(
            UnsubscribeWorkbookUseCaseIn(
                workbookId = workbookId,
                memberId = memberId,
                opinion = body.opinion
            )
        )

        return ApiResponseGenerator.success(HttpStatus.OK)
    }

    @PostMapping("/subscriptions/unsubs")
    fun deactivateAllSubscriptions(
        @AuthenticationPrincipal userDetails: TokenUserDetails,
        @Valid @RequestBody
        body: UnsubscribeAllRequest,
    ): ApiResponse<ApiResponse.Success> {
        val memberId = userDetails.username.toLong()
        unsubscribeAllUseCase.execute(
            UnsubscribeAllUseCaseIn(memberId = memberId, opinion = body.opinion)
        )

        return ApiResponseGenerator.success(HttpStatus.OK)
    }

    @PatchMapping("/subscriptions/time")
    fun updateSubscriptionTime(
        @AuthenticationPrincipal userDetails: TokenUserDetails,
        @Valid @RequestBody
        body: UpdateSubscriptionTimeRequest,
    ): ApiResponse<ApiResponse.Success> {
        UpdateSubscriptionTimeUseCaseIn(
            memberId = userDetails.username.toLong(),
            time = body.time,
            workbookId = body.workbookId
        ).let {
            updateSubscriptionTimeUseCase.execute(it)
        }
        return ApiResponseGenerator.success(HttpStatus.OK)
    }

    @PatchMapping("/subscriptions/day")
    fun updateSubscriptionDay(
        @AuthenticationPrincipal userDetails: TokenUserDetails,
        @Valid @RequestBody
        body: UpdateSubscriptionDayRequest,
    ): ApiResponse<ApiResponse.Success> {
        val dayCode = DayCode.fromCode(body.dayCode)
        dayCode.also {
            if (!(it == (DayCode.MON_TUE_WED_THU_FRI_SAT_SUN) || it == (DayCode.MON_TUE_WED_THU_FRI))) {
                throw IllegalArgumentException("Invalid day code")
            }
        }
        UpdateSubscriptionDayUseCaseIn(
            memberId = userDetails.username.toLong(),
            dayCode = dayCode,
            workbookId = body.workbookId
        ).let {
            updateSubscriptionDayUseCase.execute(it)
        }
        return ApiResponseGenerator.success(HttpStatus.OK)
    }
}