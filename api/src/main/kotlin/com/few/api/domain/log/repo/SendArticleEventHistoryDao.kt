package com.few.api.domain.log.repo

import com.few.api.domain.log.repo.command.InsertEventCommand
import com.few.api.domain.log.repo.query.SelectEventByMessageIdAndEventTypeQuery
import com.few.api.domain.log.repo.record.SendArticleEventHistoryRecord
import jooq.jooq_dsl.tables.SendArticleEventHistory
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class SendArticleEventHistoryDao(
    private val dslContext: DSLContext,
) {
    fun insertEvent(command: InsertEventCommand) {
        dslContext
            .insertInto(SendArticleEventHistory.SEND_ARTICLE_EVENT_HISTORY)
            .set(SendArticleEventHistory.SEND_ARTICLE_EVENT_HISTORY.MEMBER_ID, command.memberId)
            .set(SendArticleEventHistory.SEND_ARTICLE_EVENT_HISTORY.ARTICLE_ID, command.articleId)
            .set(SendArticleEventHistory.SEND_ARTICLE_EVENT_HISTORY.MESSAGE_ID, command.messageId)
            .set(SendArticleEventHistory.SEND_ARTICLE_EVENT_HISTORY.EVENT_TYPE_CD, command.eventType)
            .set(SendArticleEventHistory.SEND_ARTICLE_EVENT_HISTORY.SEND_TYPE_CD, command.sendType)
            .execute()
    }

    fun selectEventByMessageId(query: SelectEventByMessageIdAndEventTypeQuery): SendArticleEventHistoryRecord? =
        dslContext
            .select(
                SendArticleEventHistory.SEND_ARTICLE_EVENT_HISTORY.MEMBER_ID.`as`(
                    SendArticleEventHistoryRecord::memberId.name,
                ),
                SendArticleEventHistory.SEND_ARTICLE_EVENT_HISTORY.ARTICLE_ID.`as`(
                    SendArticleEventHistoryRecord::articleId.name,
                ),
                SendArticleEventHistory.SEND_ARTICLE_EVENT_HISTORY.MESSAGE_ID.`as`(
                    SendArticleEventHistoryRecord::messageId.name,
                ),
                SendArticleEventHistory.SEND_ARTICLE_EVENT_HISTORY.EVENT_TYPE_CD.`as`(
                    SendArticleEventHistoryRecord::eventType.name,
                ),
                SendArticleEventHistory.SEND_ARTICLE_EVENT_HISTORY.SEND_TYPE_CD.`as`(
                    SendArticleEventHistoryRecord::sendType.name,
                ),
            ).from(SendArticleEventHistory.SEND_ARTICLE_EVENT_HISTORY)
            .where(SendArticleEventHistory.SEND_ARTICLE_EVENT_HISTORY.MESSAGE_ID.eq(query.messageId))
            .and(SendArticleEventHistory.SEND_ARTICLE_EVENT_HISTORY.EVENT_TYPE_CD.eq(query.eventType))
            .fetchOne()
            ?.into(SendArticleEventHistoryRecord::class.java)
}