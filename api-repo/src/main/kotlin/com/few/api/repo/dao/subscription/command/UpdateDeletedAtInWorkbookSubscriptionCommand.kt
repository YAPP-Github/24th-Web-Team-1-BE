package com.few.api.repo.dao.subscription.command

data class UpdateDeletedAtInWorkbookSubscriptionCommand(
    val workbookId: Long,
    val memberId: Long
)