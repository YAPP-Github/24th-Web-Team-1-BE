package com.few.api.domain.log.repo.record

data class SendArticleEventHistoryRecord(
    val memberId: Long,
    val articleId: Long,
    val messageId: String,
    val eventType: Byte,
    val sendType: Byte,
)