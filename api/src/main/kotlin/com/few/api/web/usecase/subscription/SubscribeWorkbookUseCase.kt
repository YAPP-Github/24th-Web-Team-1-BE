package com.few.api.web.usecase.subscription

import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.command.InsertWorkbookSubscriptionCommand
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

        subscriptionDao.insertWorkbookSubscription(
            InsertWorkbookSubscriptionCommand(memberId = useCaseIn.memberId, workbookId = useCaseIn.workbookId)
        )
    }
}