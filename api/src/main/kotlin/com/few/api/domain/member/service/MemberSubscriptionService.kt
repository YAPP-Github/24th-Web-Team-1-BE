package com.few.api.domain.member.service

import com.few.api.domain.member.service.dto.DeleteSubscriptionDto
import com.few.api.domain.subscription.repo.SubscriptionDao
import com.few.api.domain.subscription.repo.command.UpdateDeletedAtInAllSubscriptionCommand
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
                opinion = dto.opinion,
            ),
        )
    }
}