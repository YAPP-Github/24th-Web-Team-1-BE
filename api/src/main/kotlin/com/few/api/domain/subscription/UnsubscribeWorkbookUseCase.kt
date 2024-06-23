package com.few.api.domain.subscription

import com.few.api.domain.member.service.MemberService
import com.few.api.domain.member.service.dto.GetMemberIdDto
import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.command.UpdateDeletedAtInWorkbookSubscriptionCommand
import com.few.api.domain.subscription.`in`.UnsubscribeWorkbookUseCaseIn
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UnsubscribeWorkbookUseCase(
    private val subscriptionDao: SubscriptionDao,
    private val memberService: MemberService
) {

    @Transactional
    fun execute(useCaseIn: UnsubscribeWorkbookUseCaseIn) {
        // TODO: request sending email

        val memberId = memberService.getMemberId(GetMemberIdDto(useCaseIn.email))

        subscriptionDao.updateDeletedAtInWorkbookSubscription(
            UpdateDeletedAtInWorkbookSubscriptionCommand(memberId = memberId, workbookId = useCaseIn.workbookId)
        )
    }
}