package com.few.api.domain.member.subscription

import com.few.api.domain.member.subscription.dto.DeleteSubscriptionDto
import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.command.UpdateDeletedAtInAllSubscriptionCommand
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberSubscriptionService(
    private val subscriptionDao: SubscriptionDao,
) {

    @Transactional
    fun deleteSubscription(dto: DeleteSubscriptionDto) {
        subscriptionDao.updateDeletedAtInAllSubscription(
            UpdateDeletedAtInAllSubscriptionCommand(
                memberId = dto.memberId,
                opinion = dto.opinion
            )
        )
    }
}