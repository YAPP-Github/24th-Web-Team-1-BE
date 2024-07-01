package com.few.api.repo.dao.subscription.record

data class WorkbookSubscriptionStatus(
    val workbookId: Long,
    val isActiveSub: Boolean,
    val day: Int
)