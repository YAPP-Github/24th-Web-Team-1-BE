package com.few.api.repo.dao.subscription.command

data class UpdateLastArticleProgressCommand(
    val memberId: Long,
    val workbookId: Long,
    val opinion: String = "receive.all",
)