package com.few.api.repo.dao.subscription.record

data class MemberWorkbookSubscriptionStatusRecord(
    val workbookId: Long,
    val isActiveSub: Boolean,
    val currentDay: Int,
    val totalDay: Int,
)