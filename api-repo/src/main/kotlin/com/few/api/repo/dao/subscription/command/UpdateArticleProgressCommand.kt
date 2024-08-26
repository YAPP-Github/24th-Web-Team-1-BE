package com.few.api.repo.dao.subscription.command

data class UpdateArticleProgressCommand(
    val memberId: Long,
    val workbookId: Long,
)