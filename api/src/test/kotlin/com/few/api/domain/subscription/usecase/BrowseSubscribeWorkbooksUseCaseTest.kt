package com.few.api.domain.subscription.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import com.few.api.domain.subscription.service.SubscriptionArticleService
import com.few.api.domain.subscription.usecase.dto.BrowseSubscribeWorkbooksUseCaseIn
import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.record.MemberWorkbookSubscriptionStatusRecord
import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class BrowseSubscribeWorkbooksUseCaseTest : BehaviorSpec({
    val log = KotlinLogging.logger {}

    lateinit var subscriptionDao: SubscriptionDao
    lateinit var subscriptionArticleService: SubscriptionArticleService
    lateinit var objectMapper: ObjectMapper
    lateinit var useCase: BrowseSubscribeWorkbooksUseCase

    beforeContainer {
        subscriptionDao = mockk<SubscriptionDao>()
        subscriptionArticleService = mockk<SubscriptionArticleService>()
        objectMapper = mockk<ObjectMapper>()
        useCase = BrowseSubscribeWorkbooksUseCase(subscriptionDao, subscriptionArticleService, objectMapper)
    }

    given("사용자 구독 정보 조회 요청이 온 상황에서") {
        `when`("사용자의 구독 정보가 있는 경우") {
            every { subscriptionDao.selectAllWorkbookSubscriptionStatus(any()) } returns listOf(
                MemberWorkbookSubscriptionStatusRecord(
                    workbookId = 1L,
                    isActiveSub = true,
                    currentDay = 1,
                    totalDay = 3
                ),
                MemberWorkbookSubscriptionStatusRecord(
                    workbookId = 2L,
                    isActiveSub = true,
                    currentDay = 2,
                    totalDay = 3
                )
            )

            every {
                subscriptionArticleService.readArticleIdByWorkbookIdAndDay(any())
            } returns 1L andThen 2L

            every { subscriptionDao.countAllWorkbookSubscription(any()) } returns mapOf(
                1L to 1,
                2L to 2
            )

            then("사용자의 구독 정보를 조회한다") {
                val useCaseIn = BrowseSubscribeWorkbooksUseCaseIn(memberId = 1L)
                useCase.execute(useCaseIn)

                verify(exactly = 1) { subscriptionDao.selectAllWorkbookSubscriptionStatus(any()) }
                verify(exactly = 1) { subscriptionDao.countAllWorkbookSubscription(any()) }
            }
        }
    }
})