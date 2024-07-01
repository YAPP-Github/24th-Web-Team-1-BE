package com.few.api.domain.subscription.usecase

import com.few.api.domain.subscription.service.MemberService
import com.few.api.domain.subscription.service.dto.ReadMemberIdDto
import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.command.UpdateDeletedAtInWorkbookSubscriptionCommand
import com.few.api.domain.subscription.usecase.`in`.UnsubscribeWorkbookUseCaseIn
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

        val memberId =
            memberService.readMemberId(ReadMemberIdDto(useCaseIn.email))?.memberId ?: throw RuntimeException("Not found member")

        subscriptionDao.updateDeletedAtInWorkbookSubscription(
            UpdateDeletedAtInWorkbookSubscriptionCommand(
                memberId = memberId,
                workbookId = useCaseIn.workbookId,
                opinion = useCaseIn.opinion
            )
        )
    }
}