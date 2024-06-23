package com.few.api.domain.subscription.usecase

import com.few.api.domain.subscription.service.MemberService
import com.few.api.domain.subscription.service.dto.GetMemberIdDto
import com.few.api.domain.subscription.usecase.`in`.UnsubscribeAllUseCaseIn
import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.command.UpdateDeletedAtInAllSubscriptionCommand
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UnsubscribeAllUseCase(
    private val subscriptionDao: SubscriptionDao,
    private val memberService: MemberService
) {

    @Transactional
    fun execute(useCaseIn: UnsubscribeAllUseCaseIn) {
        // TODO: request sending email

        val memberId = memberService.getMemberId(GetMemberIdDto(useCaseIn.email))

        subscriptionDao.updateDeletedAtInAllSubscription(
            UpdateDeletedAtInAllSubscriptionCommand(memberId = memberId, opinion = useCaseIn.opinion)
        )
    }
}