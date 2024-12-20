package com.few.api.domain.subscription.repo.command

data class UpdateLastArticleProgressCommand(
    val memberId: Long,
    val workbookId: Long,
    val opinion: String = "receive.all",
)