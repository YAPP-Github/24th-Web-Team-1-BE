package com.few.api.repo.dao.subscription.command

data class BulkUpdateSubscriptionSendDayCommand(
    val memberId: Long,
    val day: String,
    val workbookIds: List<Long>,
)