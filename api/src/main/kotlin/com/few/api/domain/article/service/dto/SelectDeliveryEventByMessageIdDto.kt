package com.few.api.domain.article.service.dto

data class SelectDeliveryEventByMessageIdDto(
    val messageId: String,
    val eventType: Byte,
)