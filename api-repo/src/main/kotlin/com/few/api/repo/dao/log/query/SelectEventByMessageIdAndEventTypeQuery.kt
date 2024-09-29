package com.few.api.repo.dao.log.query

data class SelectEventByMessageIdAndEventTypeQuery(
    val messageId: String,
    val eventType: Byte,
)