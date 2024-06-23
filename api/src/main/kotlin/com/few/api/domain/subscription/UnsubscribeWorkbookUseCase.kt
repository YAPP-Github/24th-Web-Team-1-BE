package com.few.api.domain.subscription

import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.command.UpdateDeletedAtInWorkbookSubscriptionCommand
import com.few.api.repo.dao.subscription.query.CountWorkbookSubscriptionQuery
import com.few.api.domain.subscription.`in`.UnsubscribeWorkbookUseCaseIn
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UnsubscribeWorkbookUseCase(
    private val subscriptionDao: SubscriptionDao
) {

    @Transactional
    fun execute(useCaseIn: UnsubscribeWorkbookUseCaseIn) {
        // TODO: request sending email

        // 구독중이 아닌지 확인
        CountWorkbookSubscriptionQuery(memberId = useCaseIn.memberId, workbookId = useCaseIn.workbookId).let { query ->
            subscriptionDao.selectCountWorkbookSubscription(query).let { cnt ->
                if (cnt == 0) {
                    throw RuntimeException("Already subscribed")
                }
            }
        }

        subscriptionDao.updateDeletedAtInWorkbookSubscription(
            UpdateDeletedAtInWorkbookSubscriptionCommand(
                memberId = useCaseIn.memberId,
                workbookId = useCaseIn.workbookId,
                opinion = useCaseIn.opinion
            )
        )
    }
}