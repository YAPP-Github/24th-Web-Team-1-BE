package com.few.api.domain.subscription.usecase

import com.few.api.domain.subscription.repo.SubscriptionDao
import com.few.api.domain.subscription.repo.command.UpdateDeletedAtInWorkbookSubscriptionCommand
import com.few.api.domain.subscription.usecase.dto.UnsubscribeWorkbookUseCaseIn
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UnsubscribeWorkbookUseCase(
    private val subscriptionDao: SubscriptionDao,
) {
    // todo add test
    @Transactional
    fun execute(useCaseIn: UnsubscribeWorkbookUseCaseIn) {
        // TODO: request sending email
        var opinion = useCaseIn.opinion
        if (useCaseIn.opinion == "") {
            opinion = "cancel"
        }

        subscriptionDao.updateDeletedAtInWorkbookSubscription(
            UpdateDeletedAtInWorkbookSubscriptionCommand(
                memberId = useCaseIn.memberId,
                workbookId = useCaseIn.workbookId,
                opinion = opinion,
            ),
        )
    }
}