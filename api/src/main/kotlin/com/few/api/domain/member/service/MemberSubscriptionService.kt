package com.few.api.domain.member.service

import com.few.api.domain.member.service.dto.DeleteSubscriptionDto
import com.few.api.domain.subscription.repo.SubscriptionDao
import com.few.api.domain.subscription.repo.command.UpdateDeletedAtInAllSubscriptionCommand
import org.springframework.stereotype.Service
import repo.jooq.DataSourceTransactional

@Service
class MemberSubscriptionService(
    private val subscriptionDao: SubscriptionDao,
) {
    @DataSourceTransactional
    fun deleteSubscription(dto: DeleteSubscriptionDto) {
        subscriptionDao.updateDeletedAtInAllSubscription(
            UpdateDeletedAtInAllSubscriptionCommand(
                memberId = dto.memberId,
                opinion = dto.opinion,
            ),
        )
    }
}