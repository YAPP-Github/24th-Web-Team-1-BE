package com.few.api.repo.dao.subscription.record

import java.time.LocalTime

data class SubscriptionSendStatusRecord(
    val workbookId: Long,
    val sendTime: LocalTime,
    val sendDay: String,
)