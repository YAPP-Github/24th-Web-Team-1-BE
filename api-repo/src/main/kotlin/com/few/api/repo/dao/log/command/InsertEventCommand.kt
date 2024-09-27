package com.few.api.repo.dao.log.command

data class InsertEventCommand(
    val memberId: Long,
    val articleId: Long,
    val messageId: String,
    val eventType: Byte,
    val sendType: Byte,
)