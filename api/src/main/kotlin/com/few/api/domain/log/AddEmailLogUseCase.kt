package com.few.api.domain.log

import com.few.api.domain.log.dto.AddEmailLogUseCaseIn
import com.few.api.repo.dao.log.SendArticleEventHistoryDao
import com.few.api.repo.dao.log.command.InsertEventCommand
import com.few.api.repo.dao.log.query.SelectEventByMessageIdAndEventTypeQuery
import com.few.api.repo.dao.member.MemberDao
import com.few.api.repo.dao.member.query.SelectMemberByEmailQuery
import com.few.api.web.support.EmailLogEventType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.webjars.NotFoundException

@Component
class AddEmailLogUseCase(
    private val memberDao: MemberDao,
    private val sendArticleEventHistoryDao: SendArticleEventHistoryDao,
) {
    @Transactional
    fun execute(useCaseIn: AddEmailLogUseCaseIn) {
        val (memberId, _, _, _) = memberDao.selectMemberByEmail(
            SelectMemberByEmailQuery(useCaseIn.destination[0])
        ) ?: throw NotFoundException("member.notfound.email")

        val record =
            sendArticleEventHistoryDao.selectEventByMessageId(
                SelectEventByMessageIdAndEventTypeQuery(
                    useCaseIn.messageId,
                    EmailLogEventType.SEND.code
                )
            )
                ?: throw IllegalStateException("event is not found")

        sendArticleEventHistoryDao.insertEvent(
            InsertEventCommand(
                memberId = memberId,
                articleId = record.articleId,
                messageId = record.messageId,
                eventType = useCaseIn.eventType.code,
                sendType = record.sendType
            )
        ).let {
//            TODO("다른 이벤트는 필요시 추가한다.")
        }
    }
}