package com.few.api.domain.subscription.usecase

import com.few.api.domain.subscription.repo.SubscriptionDao
import com.few.api.domain.subscription.usecase.dto.UnsubscribeAllUseCaseIn
import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify

class UnsubscribeAllUseCaseTest :
    BehaviorSpec({
        val log = KotlinLogging.logger {}

        lateinit var subscriptionDao: SubscriptionDao
        lateinit var useCase: UnsubscribeAllUseCase

        beforeContainer {
            subscriptionDao = mockk<SubscriptionDao>()
            useCase = UnsubscribeAllUseCase(subscriptionDao)
        }

        given("구독 취소 의견이 포함된 전체 구독 취소 요청이 온 상황에서") {
            val memberId = 1L
            val opinion = "취소합니다."
            val useCaseIn = UnsubscribeAllUseCaseIn(memberId = memberId, opinion = opinion)

            `when`("멤버의 구독 히스토리가 있는 경우") {
                every { subscriptionDao.updateDeletedAtInAllSubscription(any()) } just Runs

                then("의견을 저장하고 구독 전체를 취소한다") {
                    useCase.execute(useCaseIn)

                    verify(exactly = 1) { subscriptionDao.updateDeletedAtInAllSubscription(any()) }
                }
            }
        }

        given("구독 취소 의견이 포함되지 않은 전체 구독 취소 요청이 온 상황에서") {
            val memberId = 1L
            val opinion = ""
            val useCaseIn = UnsubscribeAllUseCaseIn(memberId = memberId, opinion = opinion)

            `when`("멤버의 구독 히스토리가 있는 경우") {
                every { subscriptionDao.updateDeletedAtInAllSubscription(any()) } just Runs

                then("의견을 cancel로 저장하고 구독 전체를 취소한다") {
                    useCase.execute(useCaseIn)

                    verify(exactly = 1) { subscriptionDao.updateDeletedAtInAllSubscription(any()) }
                }
            }
        }
    })