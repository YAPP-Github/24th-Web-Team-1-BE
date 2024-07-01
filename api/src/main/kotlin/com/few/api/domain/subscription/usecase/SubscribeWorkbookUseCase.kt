package com.few.api.domain.subscription.usecase

import com.few.api.domain.subscription.event.dto.WorkbookSubscriptionEvent
import com.few.api.domain.subscription.service.MemberService
import com.few.api.domain.subscription.service.dto.InsertMemberDto
import com.few.api.domain.subscription.service.dto.ReadMemberIdDto
import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.command.InsertWorkbookSubscriptionCommand
import com.few.api.repo.dao.subscription.query.SelectAllWorkbookSubscriptionStatusQueryNotConsiderDeletedAt
import com.few.api.domain.subscription.usecase.`in`.SubscribeWorkbookUseCaseIn
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

    // todo 이미 가입된 경우
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

        /** 구독 히스토리가 있는지 확인 */
        SelectAllWorkbookSubscriptionStatusQueryNotConsiderDeletedAt(memberId = memberId, workbookId = subTargetWorkbookId).let { query ->
            subscriptionDao.selectAllWorkbookSubscriptionStatus(query).let { subscriptionStatusList ->
                /** 이미 구독한 경우가 있는 경우 */
                if (subscriptionStatusList.isNotEmpty()) {
                    subscriptionStatusList.stream().filter { status ->
                        status.id == query.workbookId
                    }.findAny().get().let { status ->
                        if (status.subHistory) {
                            CountWorkbookMappedArticlesQuery(subTargetWorkbookId).let { query ->
                                subscriptionDao.countWorkbookMappedArticles(query)
                            }?.let { lastDay ->
                                /** 이미 학습을 완료한 경우 */
                                if (lastDay <= (status.day)) {
                                    throw RuntimeException("이미 학습을 완료한 워크북입니다.")
                                }
                                /** 재구독 */
                                subscriptionDao.reSubscribeWorkbookSubscription(command)
                            }
                        }
                    }
                } else {
                    /** 구독한 경우가 없는 경우 */
                    subscriptionDao.insertWorkbookSubscription(command)
                }
                applicationEventPublisher.publishEvent(WorkbookSubscriptionEvent(workbookId = subTargetWorkbookId))
            }
        }
    }
}