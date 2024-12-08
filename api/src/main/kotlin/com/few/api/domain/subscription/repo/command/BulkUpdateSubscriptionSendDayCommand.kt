package com.few.api.domain.subscription.repo.command

data class BulkUpdateSubscriptionSendDayCommand(
    val memberId: Long,
    val day: String,
    val workbookIds: List<Long>,
)