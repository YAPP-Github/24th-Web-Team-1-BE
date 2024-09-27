package com.few.api.domain.subscription.service.dto

data class InsertSendEventDto(
    val memberId: Long,
    val articleId: Long,
    val messageId: String,
    val sendType: Byte,
)