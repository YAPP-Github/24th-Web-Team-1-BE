package com.few.api.domain.subscription.usecase

import com.few.api.domain.subscription.usecase.dto.UnsubscribeWorkbookUseCaseIn
import com.few.api.domain.subscription.repo.SubscriptionDao
import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.*

class UnsubscribeWorkbookUseCaseTest : BehaviorSpec({
    val log = KotlinLogging.logger {}

    lateinit var subscriptionDao: SubscriptionDao
    lateinit var useCase: UnsubscribeWorkbookUseCase

    beforeContainer {
        subscriptionDao = mockk<SubscriptionDao>()
        useCase = UnsubscribeWorkbookUseCase(subscriptionDao)
    }

    given("구독 취소 의견이 포함된 특정 워크북 구독 취소 요청이 온 상황에서") {
        val memberId = 1L
        val workbookId = 1L
        val opinion = "취소합니다."
        val useCaseIn = UnsubscribeWorkbookUseCaseIn(memberId = memberId, workbookId = workbookId, opinion = opinion)

        `when`("멤버의 특정 워크북 구독 히스토리가 있는 경우") {
            every { subscriptionDao.updateDeletedAtInWorkbookSubscription(any()) } just Runs

            then("의견을 저장하고 구독 전체를 취소한다") {
                useCase.execute(useCaseIn)

                verify(exactly = 1) { subscriptionDao.updateDeletedAtInWorkbookSubscription(any()) }
            }
        }
    }

    given("구독 취소 의견이 포함되지 않은 특정 워크북 구독 취소 요청이 온 상황에서") {
        val memberId = 1L
        val workbookId = 1L
        val opinion = ""
        val useCaseIn = UnsubscribeWorkbookUseCaseIn(memberId = memberId, workbookId = workbookId, opinion = opinion)

        `when`("멤버의 특정 워크북 구독 히스토리가 있는 경우") {
            every { subscriptionDao.updateDeletedAtInWorkbookSubscription(any()) } just Runs

            then("의견을 cancel로 저장하고 특정 위크북에 대한 구독을 취소한다") {
                useCase.execute(useCaseIn)

                verify(exactly = 1) { subscriptionDao.updateDeletedAtInWorkbookSubscription(any()) }
            }
        }
    }
})