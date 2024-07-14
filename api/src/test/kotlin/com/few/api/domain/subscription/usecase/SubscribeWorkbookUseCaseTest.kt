package com.few.api.domain.subscription.usecase

import com.few.api.domain.subscription.event.dto.WorkbookSubscriptionEvent
import com.few.api.domain.subscription.service.MemberService
import com.few.api.domain.subscription.service.dto.MemberIdOutDto
import com.few.api.domain.subscription.usecase.dto.SubscribeWorkbookUseCaseIn
import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.record.WorkbookSubscriptionStatus
import io.mockk.*
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher

@ExtendWith(MockKExtension::class)
class SubscribeWorkbookUseCaseTest {
    val subscriptionDao = mockk<SubscriptionDao>()

    val memberService = mockk<MemberService>()

    val applicationEventPublisher = mockk<ApplicationEventPublisher>()

    val useCase = SubscribeWorkbookUseCase(subscriptionDao, memberService, applicationEventPublisher)

    @Test
    fun `subscriptionStatus가 null일 경우 신규 구독을 추가한다`() {
        // given
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

        // when
        useCase.execute(useCaseIn)

        // then
        verify(exactly = 1) { memberService.insertMember(any()) }
        verify(exactly = 1) { subscriptionDao.insertWorkbookSubscription(any()) }
        verify(exactly = 0) { subscriptionDao.countWorkbookMappedArticles(any()) }
        verify(exactly = 0) { subscriptionDao.reSubscribeWorkbookSubscription(any()) }
        verify(exactly = 1) { applicationEventPublisher.publishEvent(event) }
    }

    @Test
    fun `구독을 취소한 경우 재구독한다`() {
        // given
        val workbookId = 1L
        val useCaseIn = SubscribeWorkbookUseCaseIn(workbookId = workbookId, email = "test@test.com")
        val day = 2 // 현재 유저가 구독 취소 전 마지막으로 읽은 day 값
        val lastDay = 3 // 워크북 고유 정보 (워크북이 구성된 day 값)
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

        // when
        useCase.execute(useCaseIn)

        // then
        verify(exactly = 1) { memberService.insertMember(any()) }
        verify(exactly = 0) { subscriptionDao.insertWorkbookSubscription(any()) }
        verify(exactly = 1) { subscriptionDao.countWorkbookMappedArticles(any()) }
        verify(exactly = 1) { subscriptionDao.reSubscribeWorkbookSubscription(any()) }
        verify(exactly = 1) { applicationEventPublisher.publishEvent(event) }
    }
}