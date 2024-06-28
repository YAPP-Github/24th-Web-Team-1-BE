package com.few.api.repo.dao.subscription.record

data class WorkbookSubscriptionStatus(
    val id: Long,
    val subHistory: Boolean,
    val day: Int
)