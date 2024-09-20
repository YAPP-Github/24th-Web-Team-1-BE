package com.few.api.repo.dao.log.record

data class SendArticleEventHistoryRecord(
    val memberId: Long,
    val articleId: Long,
    val messageId: String,
    val eventType: Byte,
    val sendType: Byte,
)