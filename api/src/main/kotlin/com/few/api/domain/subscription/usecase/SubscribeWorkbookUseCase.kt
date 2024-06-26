package com.few.api.domain.subscription.usecase

import com.few.api.domain.subscription.service.MemberService
import com.few.api.domain.subscription.service.dto.InsertMemberDto
import com.few.api.domain.subscription.service.dto.ReadMemberIdDto
import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.command.InsertWorkbookSubscriptionCommand
import com.few.api.repo.dao.subscription.query.CountWorkbookSubscriptionQuery
import com.few.api.domain.subscription.usecase.`in`.SubscribeWorkbookUseCaseIn
import com.few.data.common.code.MemberType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class SubscribeWorkbookUseCase(
    private val subscriptionDao: SubscriptionDao,
    private val memberService: MemberService
) {

    // todo 이미 가입된 경우
    @Transactional
    fun execute(useCaseIn: SubscribeWorkbookUseCaseIn) {
        // TODO: request sending email

        val memberId = memberService.readMemberId(ReadMemberIdDto(useCaseIn.email))?.memberId ?: memberService.insertMember(
            InsertMemberDto(email = useCaseIn.email, memberType = MemberType.NORMAL)
        )

        // 이미 구독중인지 확인
        CountWorkbookSubscriptionQuery(memberId = memberId, workbookId = useCaseIn.workbookId).let { query ->
            subscriptionDao.selectCountWorkbookSubscription(query).let { cnt ->
                if (cnt > 0) {
                    throw RuntimeException("Already subscribed")
                }
            }
        }

        subscriptionDao.insertWorkbookSubscription(
            InsertWorkbookSubscriptionCommand(memberId = memberId, workbookId = useCaseIn.workbookId)
        )
    }
}