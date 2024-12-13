package com.few.api.domain.log.repo.command

data class InsertEventCommand(
    val memberId: Long,
    val articleId: Long,
    val messageId: String,
    val eventType: Byte,
    val sendType: Byte,
)