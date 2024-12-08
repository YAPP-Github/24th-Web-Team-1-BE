package com.few.api.domain.log.repo.query

data class SelectEventByMessageIdAndEventTypeQuery(
    val messageId: String,
    val eventType: Byte,
)