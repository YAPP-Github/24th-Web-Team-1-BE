package com.few.api.domain.subscription.usecase

import com.few.api.domain.subscription.event.dto.WorkbookSubscriptionEvent
import com.few.api.domain.subscription.service.MemberService
import com.few.api.domain.subscription.service.dto.InsertMemberDto
import com.few.api.domain.subscription.service.dto.ReadMemberIdDto
import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.command.InsertWorkbookSubscriptionCommand
import com.few.api.repo.dao.subscription.query.SelectAllWorkbookSubscriptionStatusQueryNotConsiderDeletedAt
import com.few.api.domain.subscription.usecase.`in`.SubscribeWorkbookUseCaseIn
import com.few.api.exception.common.NotFoundException
import com.few.api.exception.subscribe.SubscribeIllegalArgumentException
import com.few.api.repo.dao.subscription.query.CountWorkbookMappedArticlesQuery
import com.few.data.common.code.MemberType
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class SubscribeWorkbookUseCase(
    private val subscriptionDao: SubscriptionDao,
    private val memberService: MemberService,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    @Transactional
    fun execute(useCaseIn: SubscribeWorkbookUseCaseIn) {
        // TODO: request sending email

        val memberId = memberService.readMemberId(ReadMemberIdDto(useCaseIn.email))?.memberId ?: memberService.insertMember(
            InsertMemberDto(email = useCaseIn.email, memberType = MemberType.NORMAL)
        )

        val subTargetWorkbookId = useCaseIn.workbookId
        val command = InsertWorkbookSubscriptionCommand(
            memberId = memberId,
            workbookId = subTargetWorkbookId
        )

        val subscriptionStatus = subscriptionDao.selectTopWorkbookSubscriptionStatus(
            SelectAllWorkbookSubscriptionStatusQueryNotConsiderDeletedAt(memberId = memberId, workbookId = subTargetWorkbookId)
        )

        when {
            /** 구독한 히스토리가 없는 경우 */
            subscriptionStatus == null -> {
                subscriptionDao.insertWorkbookSubscription(command)
            }

            /** 이미 구독한 히스토리가 있고 구독이 취소된 경우 */
            !subscriptionStatus.isActiveSub -> {
                val lastDay = subscriptionDao.countWorkbookMappedArticles(CountWorkbookMappedArticlesQuery(subTargetWorkbookId)) ?: throw NotFoundException("workbook.notfound.id")
                if (lastDay <= subscriptionStatus.day) {
                    throw SubscribeIllegalArgumentException("subscribe.state.end")
                }
                /** 재구독 */
                subscriptionDao.reSubscribeWorkbookSubscription(command)
            }

            /** 이미 구독한 히스토리가 있고 구독이 취소되지 않은 경우 */
            else -> {
                throw SubscribeIllegalArgumentException("subscribe.state.subscribed")
            }
        }
        applicationEventPublisher.publishEvent(WorkbookSubscriptionEvent(workbookId = subTargetWorkbookId))
    }
}