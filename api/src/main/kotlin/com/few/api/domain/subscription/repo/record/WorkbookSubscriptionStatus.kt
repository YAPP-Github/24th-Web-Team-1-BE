package com.few.api.domain.subscription.repo.record

data class WorkbookSubscriptionStatus(
    val workbookId: Long,
    val isActiveSub: Boolean,
    val day: Int,
)