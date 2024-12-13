package com.few.api.domain.subscription.repo.command

data class UpdateDeletedAtInWorkbookSubscriptionCommand(
    val workbookId: Long,
    val memberId: Long,
    val opinion: String,
)