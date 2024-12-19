package com.few.api.domain.subscription.usecase

import com.few.api.domain.subscription.repo.SubscriptionDao
import com.few.api.domain.subscription.repo.command.UpdateDeletedAtInAllSubscriptionCommand
import com.few.api.domain.subscription.usecase.dto.UnsubscribeAllUseCaseIn
import org.springframework.stereotype.Component
import repo.jooq.DataSourceTransactional

@Component
class UnsubscribeAllUseCase(
    private val subscriptionDao: SubscriptionDao,
) {
    @DataSourceTransactional
    fun execute(useCaseIn: UnsubscribeAllUseCaseIn) {
        // TODO: request sending email
        var opinion = useCaseIn.opinion
        if (useCaseIn.opinion == "") {
            opinion = "cancel"
        }

        subscriptionDao.updateDeletedAtInAllSubscription(
            UpdateDeletedAtInAllSubscriptionCommand(
                memberId = useCaseIn.memberId,
                opinion = opinion,
            ),
        )
    }
}