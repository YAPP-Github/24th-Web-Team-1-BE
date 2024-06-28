package com.few.api.domain.subscription.usecase

import com.few.api.domain.subscription.service.MemberService
import com.few.api.domain.subscription.service.dto.InsertMemberDto
import com.few.api.domain.subscription.service.dto.ReadMemberIdDto
import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.command.InsertWorkbookSubscriptionCommand
import com.few.api.repo.dao.subscription.query.SelectAllWorkbookSubscriptionStatusQueryNotConsiderDeletedAt
import com.few.api.domain.subscription.usecase.`in`.SubscribeWorkbookUseCaseIn
import com.few.api.repo.dao.subscription.query.CountWorkbookMappedArticlesQuery
import com.few.data.common.code.MemberType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class SubscribeWorkbookUseCase(
    private val subscriptionDao: SubscriptionDao,
    private val memberService: MemberService
) {

    @Transactional
    fun execute(useCaseIn: SubscribeWorkbookUseCaseIn) {
        // TODO: request sending email

        val memberId = memberService.readMemberId(ReadMemberIdDto(useCaseIn.email)) ?: memberService.insertMember(
            InsertMemberDto(useCaseIn.email, MemberType.NORMAL)
        )

        val subTargetWorkbookId = useCaseIn.workbookId
        SelectAllWorkbookSubscriptionStatusQueryNotConsiderDeletedAt(memberId = memberId, workbookId = subTargetWorkbookId).let { query ->
            subscriptionDao.selectAllWorkbookSubscriptionStatus(query).let { subscriptionStatusList ->
                if (subscriptionStatusList.isNotEmpty()) {
                    subscriptionStatusList.stream().filter { status ->
                        status.id == query.workbookId
                    }.findAny().get().let { status ->
                        InsertWorkbookSubscriptionCommand(memberId = memberId, workbookId = subTargetWorkbookId).let { command ->
                            if (status.subHistory) {
                                CountWorkbookMappedArticlesQuery(subTargetWorkbookId).let { query ->
                                    subscriptionDao.countWorkbookMappedArticles(query)
                                }?.let { lastDay ->
                                    if (lastDay <= (status.day)) {
                                        throw RuntimeException("이미 학습을 완료한 워크북입니다.")
                                    }
                                    subscriptionDao.reSubscribeWorkbookSubscription(command)
                                }
                            } else {
                                subscriptionDao.insertWorkbookSubscription(command)
                            }
                        }
                    }
                }
            }
        }
    }
}