package com.few.api.domain.subscription.service

import com.few.api.domain.subscription.service.dto.InsertSendEventDto
import com.few.api.repo.dao.log.SendArticleEventHistoryDao
import com.few.api.repo.dao.log.command.InsertEventCommand
import com.few.api.web.support.EmailLogEventType
import org.springframework.stereotype.Service

@Service
class SubscriptionLogService(
    private val sendArticleEventHistoryDao: SendArticleEventHistoryDao,
) {

    fun insertSendEvent(dto: InsertSendEventDto) {
        sendArticleEventHistoryDao.insertEvent(
            InsertEventCommand(
                memberId = dto.memberId,
                articleId = dto.articleId,
                messageId = dto.messageId,
                eventType = EmailLogEventType.SEND.code,
                sendType = dto.sendType
            )
        )
    }
}