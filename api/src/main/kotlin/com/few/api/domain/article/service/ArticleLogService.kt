package com.few.api.domain.article.service

import com.few.api.domain.article.service.dto.InsertOpenEventDto
import com.few.api.domain.article.service.dto.SelectDeliveryEventByMessageIdDto
import com.few.api.domain.log.repo.SendArticleEventHistoryDao
import com.few.api.domain.log.repo.command.InsertEventCommand
import com.few.api.domain.log.repo.query.SelectEventByMessageIdAndEventTypeQuery
import com.few.api.domain.log.repo.record.SendArticleEventHistoryRecord
import org.springframework.stereotype.Service

@Service
class ArticleLogService(
    private val sendArticleEventHistoryDao: SendArticleEventHistoryDao,
) {

    fun selectDeliveryEventByMessageId(dto: SelectDeliveryEventByMessageIdDto): SendArticleEventHistoryRecord? {
        return sendArticleEventHistoryDao.selectEventByMessageId(
            SelectEventByMessageIdAndEventTypeQuery(
                dto.messageId,
                dto.eventType
            )
        )
    }

    fun insertOpenEvent(dto: InsertOpenEventDto) {
        sendArticleEventHistoryDao.insertEvent(
            InsertEventCommand(
                memberId = dto.memberId,
                articleId = dto.articleId,
                messageId = dto.messageId,
                eventType = dto.eventType,
                sendType = dto.sendType
            )
        )
    }
}