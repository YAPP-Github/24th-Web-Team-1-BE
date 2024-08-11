package com.few.api.domain.subscription.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import com.few.api.domain.subscription.service.SubscriptionArticleService
import com.few.api.domain.subscription.usecase.dto.BrowseSubscribeWorkbooksUseCaseIn
import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.record.MemberWorkbookSubscriptionStatusRecord
import com.few.api.web.support.WorkBookStatus
import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
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

    given("멤버의 구독 워크북 정보 조회 요청이 온 상황에서") {
        val memberId = 1L
        val useCaseIn = BrowseSubscribeWorkbooksUseCaseIn(memberId = memberId)

        `when`("멤버의 구독 워크북 정보가 존재할 경우") {
            val inactiveWorkbookId = 1L
            val inactiveWorkbookCurrentDay = 2
            val inactiveWorkbookTotalDay = 3
            every { subscriptionDao.selectAllInActiveWorkbookSubscriptionStatus(any()) } returns listOf(
                MemberWorkbookSubscriptionStatusRecord(
                    workbookId = inactiveWorkbookId,
                    isActiveSub = false,
                    currentDay = inactiveWorkbookCurrentDay,
                    totalDay = inactiveWorkbookTotalDay
                )
            )

            val activeWorkbookId = 2L
            val activeWorkbookCurrentDay = 1
            val activeWorkbookTotalDay = 3
            every { subscriptionDao.selectAllActiveWorkbookSubscriptionStatus(any()) } returns listOf(
                MemberWorkbookSubscriptionStatusRecord(
                    workbookId = activeWorkbookId,
                    isActiveSub = true,
                    currentDay = activeWorkbookCurrentDay,
                    totalDay = activeWorkbookTotalDay
                )

            )

            every {
                subscriptionArticleService.readArticleIdByWorkbookIdAndDay(any())
            } returns inactiveWorkbookId andThen activeWorkbookId

            val activeWorkbookSubscriptionCount = 1
            val inactiveWorkbookSubscriptionCount = 2
            every { subscriptionDao.countAllWorkbookSubscription(any()) } returns mapOf(
                inactiveWorkbookId to inactiveWorkbookSubscriptionCount,
                activeWorkbookId to activeWorkbookSubscriptionCount
            )

            every { objectMapper.writeValueAsString(any()) } returns "{\"articleId\":$inactiveWorkbookId}" andThen "{\"articleId\":$activeWorkbookId}"

            then("멤버의 구독 워크북 정보를 반환한다") {
                val useCaseOut = useCase.execute(useCaseIn)
                useCaseOut.workbooks.size shouldBe 2

                val inActiveSubscriptionWorkbook = useCaseOut.workbooks[0]
                inActiveSubscriptionWorkbook.workbookId shouldBe inactiveWorkbookId
                inActiveSubscriptionWorkbook.isActiveSub shouldBe WorkBookStatus.DONE
                inActiveSubscriptionWorkbook.currentDay shouldBe inactiveWorkbookCurrentDay
                inActiveSubscriptionWorkbook.totalDay shouldBe inactiveWorkbookTotalDay
                inActiveSubscriptionWorkbook.totalSubscriber shouldBe inactiveWorkbookSubscriptionCount
                inActiveSubscriptionWorkbook.articleInfo shouldBe "{\"articleId\":$inactiveWorkbookId}"

                val activeSubscriptionWorkbook = useCaseOut.workbooks[1]
                activeSubscriptionWorkbook.workbookId shouldBe activeWorkbookId
                activeSubscriptionWorkbook.isActiveSub shouldBe WorkBookStatus.ACTIVE
                activeSubscriptionWorkbook.currentDay shouldBe activeWorkbookCurrentDay
                activeSubscriptionWorkbook.totalDay shouldBe activeWorkbookTotalDay
                activeSubscriptionWorkbook.totalSubscriber shouldBe activeWorkbookSubscriptionCount
                activeSubscriptionWorkbook.articleInfo shouldBe "{\"articleId\":$activeWorkbookId}"

                verify(exactly = 1) { subscriptionDao.selectAllInActiveWorkbookSubscriptionStatus(any()) }
                verify(exactly = 1) { subscriptionDao.selectAllActiveWorkbookSubscriptionStatus(any()) }
                verify(exactly = 2) { subscriptionArticleService.readArticleIdByWorkbookIdAndDay(any()) }
                verify(exactly = 1) { subscriptionDao.countAllWorkbookSubscription(any()) }
                verify(exactly = 2) { objectMapper.writeValueAsString(any()) }
            }
        }

        `when`("멤버의 구독 비활성 워크북 정보만 존재할 경우") {
            val inactiveWorkbookId = 1L
            val inactiveWorkbookCurrentDay = 2
            val inactiveWorkbookTotalDay = 3
            every { subscriptionDao.selectAllInActiveWorkbookSubscriptionStatus(any()) } returns listOf(
                MemberWorkbookSubscriptionStatusRecord(
                    workbookId = inactiveWorkbookId,
                    isActiveSub = false,
                    currentDay = inactiveWorkbookCurrentDay,
                    totalDay = inactiveWorkbookTotalDay
                )
            )

            every { subscriptionDao.selectAllActiveWorkbookSubscriptionStatus(any()) } returns emptyList()

            every {
                subscriptionArticleService.readArticleIdByWorkbookIdAndDay(any())
            } returns inactiveWorkbookId

            val inactiveWorkbookSubscriptionCount = 2
            every { subscriptionDao.countAllWorkbookSubscription(any()) } returns mapOf(
                inactiveWorkbookId to inactiveWorkbookSubscriptionCount
            )

            every { objectMapper.writeValueAsString(any()) } returns "{\"articleId\":$inactiveWorkbookId}"

            then("멤버의 구독 비활성 워크북 정보를 반환한다") {
                val useCaseOut = useCase.execute(useCaseIn)
                useCaseOut.workbooks.size shouldBe 1

                val inActiveSubscriptionWorkbook = useCaseOut.workbooks[0]
                inActiveSubscriptionWorkbook.workbookId shouldBe inactiveWorkbookId
                inActiveSubscriptionWorkbook.isActiveSub shouldBe WorkBookStatus.DONE
                inActiveSubscriptionWorkbook.currentDay shouldBe inactiveWorkbookCurrentDay
                inActiveSubscriptionWorkbook.totalDay shouldBe inactiveWorkbookTotalDay
                inActiveSubscriptionWorkbook.totalSubscriber shouldBe inactiveWorkbookSubscriptionCount
                inActiveSubscriptionWorkbook.articleInfo shouldBe "{\"articleId\":$inactiveWorkbookId}"

                verify(exactly = 1) { subscriptionDao.selectAllInActiveWorkbookSubscriptionStatus(any()) }
                verify(exactly = 1) { subscriptionDao.selectAllActiveWorkbookSubscriptionStatus(any()) }
                verify(exactly = 1) { subscriptionArticleService.readArticleIdByWorkbookIdAndDay(any()) }
                verify(exactly = 1) { subscriptionDao.countAllWorkbookSubscription(any()) }
                verify(exactly = 1) { objectMapper.writeValueAsString(any()) }
            }
        }

        `when`("멤버의 구독 활성 워크북 정보만 존재할 경우") {
            every { subscriptionDao.selectAllInActiveWorkbookSubscriptionStatus(any()) } returns emptyList()

            val activeWorkbookId = 1L
            val activeWorkbookCurrentDay = 2
            val activeWorkbookTotalDay = 3
            every { subscriptionDao.selectAllActiveWorkbookSubscriptionStatus(any()) } returns listOf(
                MemberWorkbookSubscriptionStatusRecord(
                    workbookId = activeWorkbookId,
                    isActiveSub = false,
                    currentDay = activeWorkbookCurrentDay,
                    totalDay = activeWorkbookTotalDay
                )
            )

            every {
                subscriptionArticleService.readArticleIdByWorkbookIdAndDay(any())
            } returns activeWorkbookId

            val activeWorkbookSubscriptionCount = 1
            every { subscriptionDao.countAllWorkbookSubscription(any()) } returns mapOf(
                activeWorkbookId to activeWorkbookSubscriptionCount
            )

            every { objectMapper.writeValueAsString(any()) } returns "{\"articleId\":$activeWorkbookId}"

            then("멤버의 구독 활성 워크북 정보를 반환한다") {
                val useCaseOut = useCase.execute(useCaseIn)
                useCaseOut.workbooks.size shouldBe 1

                val inActiveSubscriptionWorkbook = useCaseOut.workbooks[0]
                inActiveSubscriptionWorkbook.workbookId shouldBe activeWorkbookId
                inActiveSubscriptionWorkbook.isActiveSub shouldBe WorkBookStatus.DONE
                inActiveSubscriptionWorkbook.currentDay shouldBe activeWorkbookCurrentDay
                inActiveSubscriptionWorkbook.totalDay shouldBe activeWorkbookTotalDay
                inActiveSubscriptionWorkbook.totalSubscriber shouldBe activeWorkbookSubscriptionCount
                inActiveSubscriptionWorkbook.articleInfo shouldBe "{\"articleId\":$activeWorkbookId}"

                verify(exactly = 1) { subscriptionDao.selectAllInActiveWorkbookSubscriptionStatus(any()) }
                verify(exactly = 1) { subscriptionDao.selectAllActiveWorkbookSubscriptionStatus(any()) }
                verify(exactly = 1) { subscriptionArticleService.readArticleIdByWorkbookIdAndDay(any()) }
                verify(exactly = 1) { subscriptionDao.countAllWorkbookSubscription(any()) }
                verify(exactly = 1) { objectMapper.writeValueAsString(any()) }
            }
        }
    }
})