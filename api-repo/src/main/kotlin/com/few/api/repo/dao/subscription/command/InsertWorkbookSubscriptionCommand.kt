package com.few.api.repo.dao.subscription.command

data class InsertWorkbookSubscriptionCommand(
    val workbookId: Long,
    val memberId: Long
)