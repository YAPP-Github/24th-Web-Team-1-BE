package com.few.api.domain.subscription.usecase

import com.few.api.domain.subscription.event.dto.WorkbookSubscriptionEvent
import com.few.api.domain.subscription.service.MemberService
import com.few.api.domain.subscription.service.dto.MemberIdOutDto
import com.few.api.domain.subscription.usecase.dto.SubscribeWorkbookUseCaseIn
import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.record.WorkbookSubscriptionStatus
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.just
import io.mockk.Runs
import org.springframework.context.ApplicationEventPublisher

class SubscribeWorkbookUseCaseTest : BehaviorSpec({

    lateinit var subscriptionDao: SubscriptionDao
    lateinit var memberService: MemberService
    lateinit var applicationEventPublisher: ApplicationEventPublisher
    lateinit var useCase: SubscribeWorkbookUseCase

    given("구독 요청이 온 상황에서") {

        beforeContainer {
            subscriptionDao = mockk<SubscriptionDao>()
            memberService = mockk<MemberService>()
            applicationEventPublisher = mockk<ApplicationEventPublisher>()
            useCase = SubscribeWorkbookUseCase(subscriptionDao, memberService, applicationEventPublisher)
        }

        `when`("subscriptionStatus가 null일 경우") {
            val workbookId = 1L
            val useCaseIn = SubscribeWorkbookUseCaseIn(workbookId = workbookId, email = "test@test.com")
            val serviceOutDto = MemberIdOutDto(memberId = 1L)
            val event = WorkbookSubscriptionEvent(workbookId)

            every { memberService.readMemberId(any()) } returns null
            every { memberService.insertMember(any()) } returns serviceOutDto
            every { subscriptionDao.selectTopWorkbookSubscriptionStatus(any()) } returns null
            every { subscriptionDao.insertWorkbookSubscription(any()) } just Runs
            every { applicationEventPublisher.publishEvent(event) } answers {
                println("Mocking applicationEventPublisher.publishEvent(any()) was called")
            }

            then("신규 구독을 추가한다") {
                useCase.execute(useCaseIn)

                verify(exactly = 1) { memberService.insertMember(any()) }
                verify(exactly = 1) { subscriptionDao.insertWorkbookSubscription(any()) }
                verify(exactly = 0) { subscriptionDao.countWorkbookMappedArticles(any()) }
                verify(exactly = 0) { subscriptionDao.reSubscribeWorkbookSubscription(any()) }
                verify(exactly = 1) { applicationEventPublisher.publishEvent(event) }
            }
        }

        `when`("구독을 취소한 경우") {
            val workbookId = 1L
            val useCaseIn = SubscribeWorkbookUseCaseIn(workbookId = workbookId, email = "test@test.com")
            val day = 2
            val lastDay = 3
            val serviceOutDto = MemberIdOutDto(memberId = 1L)
            val subscriptionStatusRecord = WorkbookSubscriptionStatus(workbookId = workbookId, isActiveSub = false, day)
            val event = WorkbookSubscriptionEvent(workbookId)

            every { memberService.readMemberId(any()) } returns null
            every { memberService.insertMember(any()) } returns serviceOutDto
            every { subscriptionDao.selectTopWorkbookSubscriptionStatus(any()) } returns subscriptionStatusRecord
            every { subscriptionDao.countWorkbookMappedArticles(any()) } returns lastDay
            every { subscriptionDao.reSubscribeWorkbookSubscription(any()) } just Runs
            every { applicationEventPublisher.publishEvent(event) } answers {
                println("Mocking applicationEventPublisher.publishEvent(any()) was called")
            }

            then("재구독한다") {
                useCase.execute(useCaseIn)

                verify(exactly = 1) { memberService.insertMember(any()) }
                verify(exactly = 0) { subscriptionDao.insertWorkbookSubscription(any()) }
                verify(exactly = 1) { subscriptionDao.countWorkbookMappedArticles(any()) }
                verify(exactly = 1) { subscriptionDao.reSubscribeWorkbookSubscription(any()) }
                verify(exactly = 1) { applicationEventPublisher.publishEvent(event) }
            }
        }

        `when`("이미 구독하고 있을 경우") {
            val workbookId = 1L
            val useCaseIn = SubscribeWorkbookUseCaseIn(workbookId = workbookId, email = "test@test.com")
            val day = 2
            val lastDay = 3
            val serviceOutDto = MemberIdOutDto(memberId = 1L)
            val subscriptionStatusRecord = WorkbookSubscriptionStatus(workbookId = workbookId, isActiveSub = true, day)
            val event = WorkbookSubscriptionEvent(workbookId)

            every { memberService.readMemberId(any()) } returns null
            every { memberService.insertMember(any()) } returns serviceOutDto
            every { subscriptionDao.selectTopWorkbookSubscriptionStatus(any()) } returns subscriptionStatusRecord
            every { subscriptionDao.countWorkbookMappedArticles(any()) } returns lastDay
            every { subscriptionDao.reSubscribeWorkbookSubscription(any()) } just Runs
            every { applicationEventPublisher.publishEvent(event) } answers {
                println("Mocking applicationEventPublisher.publishEvent(any()) was called")
            }

            then("예외가 발생한다") {
                shouldThrow<Exception> { useCase.execute(useCaseIn) }

                verify(exactly = 1) { memberService.insertMember(any()) }
                verify(exactly = 0) { subscriptionDao.insertWorkbookSubscription(any()) }
                verify(exactly = 0) { subscriptionDao.countWorkbookMappedArticles(any()) }
                verify(exactly = 0) { subscriptionDao.reSubscribeWorkbookSubscription(any()) }
                verify(exactly = 0) { applicationEventPublisher.publishEvent(event) }
            }
        }
    }
})