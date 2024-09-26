package com.few.api.domain.subscription.usecase

import com.few.api.domain.subscription.event.dto.WorkbookSubscriptionEvent
import com.few.api.domain.subscription.usecase.dto.SubscribeWorkbookUseCaseIn
import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.record.SubscriptionSendStatus
import com.few.api.repo.dao.subscription.record.WorkbookSubscriptionStatus
import com.few.data.common.code.DayCode
import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.just
import io.mockk.Runs
import org.springframework.context.ApplicationEventPublisher
import java.time.LocalTime

class SubscribeWorkbookUseCaseTest : BehaviorSpec({
    val log = KotlinLogging.logger {}

    lateinit var subscriptionDao: SubscriptionDao
    lateinit var applicationEventPublisher: ApplicationEventPublisher
    lateinit var useCase: SubscribeWorkbookUseCase

    beforeContainer {
        subscriptionDao = mockk<SubscriptionDao>()
        applicationEventPublisher = mockk<ApplicationEventPublisher>()
        useCase = SubscribeWorkbookUseCase(subscriptionDao, applicationEventPublisher)
    }

    given("멤버의 워크북 구독 요청이 온 상황에서") {
        val workbookId = 1L
        val memberId = 1L
        val useCaseIn = SubscribeWorkbookUseCaseIn(workbookId = workbookId, memberId = memberId)

        `when`("멤버의 구독 히스토리가 없는 경우") {
            every { subscriptionDao.selectSubscriptionSendStatus(any()) } returns listOf(
                SubscriptionSendStatus(
                    workbookId = workbookId,
                    memberId = memberId,
                    sendDay = DayCode.MON_TUE_WED_THU_FRI.code,
                    sendTime = LocalTime.of(8, 0)
                )
            )

            every { subscriptionDao.selectTopWorkbookSubscriptionStatus(any()) } returns null

            every { subscriptionDao.insertWorkbookSubscription(any()) } just Runs

            val event = WorkbookSubscriptionEvent(workbookId, memberId, 1)
            every { applicationEventPublisher.publishEvent(event) } answers {
                log.debug { "Mocking applicationEventPublisher.publishEvent(any()) was called" }
            }

            then("구독한다") {
                useCase.execute(useCaseIn)

                verify(exactly = 1) { subscriptionDao.selectTopWorkbookSubscriptionStatus(any()) }
                verify(exactly = 1) { subscriptionDao.selectTopWorkbookSubscriptionStatus(any()) }
                verify(exactly = 1) { subscriptionDao.insertWorkbookSubscription(any()) }
                verify(exactly = 0) { subscriptionDao.countWorkbookMappedArticles(any()) }
                verify(exactly = 0) { subscriptionDao.reSubscribeWorkbookSubscription(any()) }
                verify(exactly = 1) { applicationEventPublisher.publishEvent(event) }
            }
        }

        `when`("이미 구독한 히스토리가 있고 구독이 취소된 경우") {
            every { subscriptionDao.selectSubscriptionSendStatus(any()) } returns listOf(
                SubscriptionSendStatus(
                    workbookId = workbookId,
                    memberId = memberId,
                    sendDay = DayCode.MON_TUE_WED_THU_FRI.code,
                    sendTime = LocalTime.of(8, 0)
                )
            )

            val day = 2
            every { subscriptionDao.selectTopWorkbookSubscriptionStatus(any()) } returns WorkbookSubscriptionStatus(
                workbookId = workbookId,
                isActiveSub = false,
                day
            )

            val lastDay = 3
            every { subscriptionDao.countWorkbookMappedArticles(any()) } returns lastDay

            every { subscriptionDao.reSubscribeWorkbookSubscription(any()) } just Runs

            val event = WorkbookSubscriptionEvent(workbookId, memberId, day)
            every { applicationEventPublisher.publishEvent(event) } answers {
                log.debug { "Mocking applicationEventPublisher.publishEvent(any()) was called" }
            }

            then("재구독한다") {
                useCase.execute(useCaseIn)

                verify(exactly = 1) { subscriptionDao.selectTopWorkbookSubscriptionStatus(any()) }
                verify(exactly = 1) { subscriptionDao.selectTopWorkbookSubscriptionStatus(any()) }
                verify(exactly = 0) { subscriptionDao.insertWorkbookSubscription(any()) }
                verify(exactly = 1) { subscriptionDao.countWorkbookMappedArticles(any()) }
                verify(exactly = 1) { subscriptionDao.reSubscribeWorkbookSubscription(any()) }
                verify(exactly = 1) { applicationEventPublisher.publishEvent(event) }
            }
        }

        `when`("이미 구독한 히스토리가 있고 구독을 완료한 경우") {
            every { subscriptionDao.selectSubscriptionSendStatus(any()) } returns listOf(
                SubscriptionSendStatus(
                    workbookId = workbookId,
                    memberId = memberId,
                    sendDay = DayCode.MON_TUE_WED_THU_FRI.code,
                    sendTime = LocalTime.of(8, 0)
                )
            )

            val day = 3
            every { subscriptionDao.selectTopWorkbookSubscriptionStatus(any()) } returns WorkbookSubscriptionStatus(
                workbookId = workbookId,
                isActiveSub = false,
                day
            )

            val lastDay = day
            every { subscriptionDao.countWorkbookMappedArticles(any()) } returns lastDay

            every { subscriptionDao.reSubscribeWorkbookSubscription(any()) } just Runs

            val event = WorkbookSubscriptionEvent(workbookId, memberId, day)
            every { applicationEventPublisher.publishEvent(event) } answers {
                log.debug { "Mocking applicationEventPublisher.publishEvent(any()) was called" }
            }

            then("예외가 발생한다") {
                shouldThrow<Exception> { useCase.execute(useCaseIn) }

                verify(exactly = 1) { subscriptionDao.selectTopWorkbookSubscriptionStatus(any()) }
                verify(exactly = 1) { subscriptionDao.selectTopWorkbookSubscriptionStatus(any()) }
                verify(exactly = 0) { subscriptionDao.insertWorkbookSubscription(any()) }
                verify(exactly = 1) { subscriptionDao.countWorkbookMappedArticles(any()) }
                verify(exactly = 0) { subscriptionDao.reSubscribeWorkbookSubscription(any()) }
                verify(exactly = 0) { applicationEventPublisher.publishEvent(event) }
            }
        }

        `when`("구독 중인 경우") {
            every { subscriptionDao.selectSubscriptionSendStatus(any()) } returns listOf(
                SubscriptionSendStatus(
                    workbookId = workbookId,
                    memberId = memberId,
                    sendDay = DayCode.MON_TUE_WED_THU_FRI.code,
                    sendTime = LocalTime.of(8, 0)
                )
            )

            val day = 2
            every { subscriptionDao.selectTopWorkbookSubscriptionStatus(any()) } returns WorkbookSubscriptionStatus(workbookId = workbookId, isActiveSub = true, day)

            then("예외가 발생한다") {
                shouldThrow<Exception> { useCase.execute(useCaseIn) }

                verify(exactly = 1) { subscriptionDao.selectTopWorkbookSubscriptionStatus(any()) }
                verify(exactly = 1) { subscriptionDao.selectTopWorkbookSubscriptionStatus(any()) }
                verify(exactly = 0) { subscriptionDao.insertWorkbookSubscription(any()) }
                verify(exactly = 0) { subscriptionDao.countWorkbookMappedArticles(any()) }
                verify(exactly = 0) { subscriptionDao.reSubscribeWorkbookSubscription(any()) }
                verify(exactly = 0) {
                    applicationEventPublisher.publishEvent(
                        WorkbookSubscriptionEvent(workbookId, memberId, day)
                    )
                }
            }
        }
    }
})