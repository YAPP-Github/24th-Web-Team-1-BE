package com.few.api.domain.subscription.usecase

import com.few.api.domain.subscription.repo.SubscriptionDao
import com.few.api.domain.subscription.repo.command.BulkUpdateSubscriptionSendTimeCommand
import com.few.api.domain.subscription.usecase.dto.UpdateSubscriptionTimeUseCaseIn
import org.springframework.stereotype.Component
import repo.jooq.DataSourceTransactional

@Component
class UpdateSubscriptionTimeUseCase(
    private val subscriptionDao: SubscriptionDao,
) {
    @DataSourceTransactional
    fun execute(useCaseIn: UpdateSubscriptionTimeUseCaseIn) {
        /**
         * workbookId기 없으면, memberId로 구독중인 모든 workbookId를 가져와서 해당하는 모든 workbookId의 구독요일을 변경한다.
         */
        useCaseIn.workbookId ?: subscriptionDao
            .selectAllActiveSubscriptionWorkbookIds(
                SubscriptionDao.SelectAllActiveSubscriptionWorkbookIdsQuery(useCaseIn.memberId),
            ).let {
                subscriptionDao.bulkUpdateSubscriptionSendTime(
                    BulkUpdateSubscriptionSendTimeCommand(
                        useCaseIn.memberId,
                        useCaseIn.time,
                        it,
                    ),
                )
            }
    }
}