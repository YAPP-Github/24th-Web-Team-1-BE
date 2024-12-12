package com.few.api.domain.subscription.repo.record

import java.time.LocalTime

data class SubscriptionSendStatusRecord(
    val workbookId: Long,
    val sendTime: LocalTime,
    val sendDay: String,
)