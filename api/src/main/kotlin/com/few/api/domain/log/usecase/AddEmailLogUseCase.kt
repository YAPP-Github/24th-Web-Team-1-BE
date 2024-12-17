package com.few.api.domain.log.usecase

import com.few.api.domain.common.exception.NotFoundException
import com.few.api.domain.common.vo.EmailLogEventType
import com.few.api.domain.log.dto.AddEmailLogUseCaseIn
import com.few.api.domain.log.repo.SendArticleEventHistoryDao
import com.few.api.domain.log.repo.command.InsertEventCommand
import com.few.api.domain.log.repo.query.SelectEventByMessageIdAndEventTypeQuery
import com.few.api.domain.member.repo.MemberDao
import com.few.api.domain.member.repo.query.SelectMemberByEmailQuery
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AddEmailLogUseCase(
    private val memberDao: MemberDao,
    private val sendArticleEventHistoryDao: SendArticleEventHistoryDao,
) {
    @Transactional
    fun execute(useCaseIn: AddEmailLogUseCaseIn) {
        val (memberId, _, _, _) =
            memberDao.selectMemberByEmail(
                SelectMemberByEmailQuery(useCaseIn.destination[0]),
            ) ?: throw NotFoundException("member.notfound.email")

        val record =
            sendArticleEventHistoryDao.selectEventByMessageId(
                SelectEventByMessageIdAndEventTypeQuery(
                    useCaseIn.messageId,
                    EmailLogEventType.SEND.code,
                ),
            )
                ?: throw IllegalStateException("event is not found")

        sendArticleEventHistoryDao
            .insertEvent(
                InsertEventCommand(
                    memberId = memberId,
                    articleId = record.articleId,
                    messageId = record.messageId,
                    eventType = useCaseIn.eventType.code,
                    sendType = record.sendType,
                ),
            ).let {
//            TODO("다른 이벤트는 필요시 추가한다.")
            }
    }
}