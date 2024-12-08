package com.few.api.domain.subscription.repo.record

data class MemberWorkbookSubscriptionStatusRecord(
    val workbookId: Long,
    val isActiveSub: Boolean,
    val currentDay: Int,
    val totalDay: Int,
)