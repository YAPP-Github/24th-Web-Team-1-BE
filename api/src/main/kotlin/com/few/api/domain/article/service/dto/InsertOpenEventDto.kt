package com.few.api.domain.article.service.dto

data class InsertOpenEventDto(
    val memberId: Long,
    val articleId: Long,
    val messageId: String,
    val eventType: Byte,
    val sendType: Byte,
)