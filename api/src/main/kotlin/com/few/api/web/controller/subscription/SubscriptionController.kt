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
import com.few.api.domain.workbook.usecase.BrowseWorkbooksUseCase
import com.few.api.domain.workbook.usecase.dto.BrowseWorkbooksUseCaseIn
import com.few.api.security.authentication.token.TokenUserDetails
import com.few.api.security.filter.token.AccessTokenResolver
import com.few.api.security.token.TokenResolver
import com.few.api.web.controller.subscription.request.UnsubscribeAllRequest
import com.few.api.web.controller.subscription.response.MainViewBrowseSubscribeWorkbooksResponse
import com.few.api.web.controller.subscription.response.MainViewSubscribeWorkbookInfo
import com.few.api.web.controller.subscription.response.SubscribeWorkbookInfo
import com.few.api.web.controller.subscription.response.SubscribeWorkbooksResponse
import com.few.api.web.support.ViewCategory
import com.few.api.web.support.WorkBookCategory
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
    private val tokenResolver: TokenResolver,

    // 임시 구현용
    private val browseWorkBooksUseCase: BrowseWorkbooksUseCase,
) {

    @GetMapping("/subscriptions/workbooks")
    fun browseSubscribeWorkbooks(
        @AuthenticationPrincipal userDetails: TokenUserDetails,
        @RequestParam(
            value = "view",
            required = false
        ) view: ViewCategory? = ViewCategory.MAIN_CARD,
    ): ApiResponse<ApiResponse.SuccessBody<SubscribeWorkbooksResponse>> {
        val memberId = userDetails.username.toLong()
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

    @GetMapping("/subscriptions/workbooks/main")
    fun mainViewBrowseSubscribeWorkbooks(
        servletRequest: HttpServletRequest,
        @RequestParam(value = "category", required = false)
        category: WorkBookCategory?,
    ): ApiResponse<ApiResponse.SuccessBody<MainViewBrowseSubscribeWorkbooksResponse>> {
        val authorization: String? = servletRequest.getHeader("Authorization")
        val memberId = authorization?.let {
            AccessTokenResolver.resolve(it)
        }.let {
            tokenResolver.resolveId(it)
        }

        if (memberId != null) {
            val memberSubscribeWorkbooks = BrowseSubscribeWorkbooksUseCaseIn(memberId).let {
                browseSubscribeWorkbooksUseCase.execute(it)
            }
            val workbooks =
                BrowseWorkbooksUseCaseIn(
                    category ?: WorkBookCategory.All,
                    ViewCategory.MAIN_CARD,
                    memberId
                ).let { useCaseIn ->
                    browseWorkBooksUseCase.execute(useCaseIn)
                }

            return MainViewBrowseSubscribeWorkbooksResponse(
                workbooks = workbooks.workbooks.map {
                    MainViewSubscribeWorkbookInfo(
                        id = it.id,
                        mainImageUrl = it.mainImageUrl,
                        title = it.title,
                        description = it.description,
                        category = it.category,
                        createdAt = it.createdAt,
                        writerDetails = it.writerDetails,
                        subscriptionCount = it.subscriptionCount,
                        status = memberSubscribeWorkbooks.workbooks.find { subscribe -> subscribe.workbookId == it.id }?.isActiveSub?.name,
                        totalDay = memberSubscribeWorkbooks.workbooks.find { subscribe -> subscribe.workbookId == it.id }?.totalDay,
                        currentDay = memberSubscribeWorkbooks.workbooks.find { subscribe -> subscribe.workbookId == it.id }?.currentDay,
                        rank = memberSubscribeWorkbooks.workbooks.find { subscribe -> subscribe.workbookId == it.id }?.rank,
                        totalSubscriber = memberSubscribeWorkbooks.workbooks.find { subscribe -> subscribe.workbookId == it.id }?.totalSubscriber,
                        articleInfo = memberSubscribeWorkbooks.workbooks.find { subscribe -> subscribe.workbookId == it.id }?.articleInfo
                    )
                }
            ).let {
                ApiResponseGenerator.success(it, HttpStatus.OK)
            }
        } else {
            val workbooks =
                BrowseWorkbooksUseCaseIn(
                    category ?: WorkBookCategory.All,
                    ViewCategory.MAIN_CARD,
                    memberId
                ).let { useCaseIn ->
                    browseWorkBooksUseCase.execute(useCaseIn)
                }

            return MainViewBrowseSubscribeWorkbooksResponse(
                workbooks = workbooks.workbooks.map {
                    MainViewSubscribeWorkbookInfo(
                        id = it.id,
                        mainImageUrl = it.mainImageUrl,
                        title = it.title,
                        description = it.description,
                        category = it.category,
                        createdAt = it.createdAt,
                        writerDetails = it.writerDetails,
                        subscriptionCount = it.subscriptionCount,
                        status = null,
                        totalDay = null,
                        currentDay = null,
                        rank = null,
                        totalSubscriber = null,
                        articleInfo = null
                    )
                }
            ).let {
                ApiResponseGenerator.success(it, HttpStatus.OK)
            }
        }
    }

    // todo fix email to memberId
    @PostMapping("/workbooks/{workbookId}/subs")
    fun subscribeWorkbook(
        @AuthenticationPrincipal userDetails: TokenUserDetails,
        @PathVariable(value = "workbookId")
        @Min(value = 1, message = "{min.id}")
        workbookId: Long,
        @Valid @RequestBody
        body: SubscribeWorkbookRequest,
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
}