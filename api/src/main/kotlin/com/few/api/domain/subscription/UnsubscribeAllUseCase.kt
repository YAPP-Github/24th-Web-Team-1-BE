package com.few.api.domain.subscription

import com.few.api.domain.subscription.`in`.UnsubscribeAllUseCaseIn
import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.command.UpdateDeletedAtInAllSubscriptionCommand
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UnsubscribeAllUseCase(
    private val subscriptionDao: SubscriptionDao
) {

    @Transactional
    fun execute(useCaseIn: UnsubscribeAllUseCaseIn) {
        // TODO: request sending email

        subscriptionDao.updateDeletedAtInAllSubscription(
            UpdateDeletedAtInAllSubscriptionCommand(memberId = useCaseIn.memberId, opinion = useCaseIn.opinion)
        )
    }
}