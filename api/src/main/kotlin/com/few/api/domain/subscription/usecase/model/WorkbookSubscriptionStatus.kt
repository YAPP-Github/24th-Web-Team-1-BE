package com.few.api.domain.subscription.usecase.model

class WorkbookSubscriptionStatus(
    val workbookId: Long,
    val isActiveSub: Boolean,
    val day: Int,
)