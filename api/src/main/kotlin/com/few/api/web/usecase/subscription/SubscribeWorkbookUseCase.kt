package com.few.api.web.usecase.subscription

import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.command.InsertWorkbookSubscriptionCommand
import com.few.api.repo.dao.subscription.query.CountWorkbookSubscriptionQuery
import com.few.api.web.usecase.subscription.`in`.SubscribeWorkbookUseCaseIn
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class SubscribeWorkbookUseCase(
    private val subscriptionDao: SubscriptionDao
) {

    @Transactional
    fun execute(useCaseIn: SubscribeWorkbookUseCaseIn) {
        // TODO: request sending email

        // 이미 구독중인지 확인
        CountWorkbookSubscriptionQuery(memberId = useCaseIn.memberId, workbookId = useCaseIn.workbookId).let { query ->
            subscriptionDao.selectCountWorkbookSubscription(query).let { cnt ->
                if (cnt > 0) {
                    throw RuntimeException("Already subscribed")
                }
            }
        }

        subscriptionDao.insertWorkbookSubscription(
            InsertWorkbookSubscriptionCommand(memberId = useCaseIn.memberId, workbookId = useCaseIn.workbookId)
        )
    }
}