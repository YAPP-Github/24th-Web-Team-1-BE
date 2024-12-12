package com.few.api.domain.subscription.repo.command

data class UpdateArticleProgressCommand(
    val memberId: Long,
    val workbookId: Long,
)