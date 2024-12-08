package com.few.api.domain.subscription.repo.record

import java.time.LocalTime

data class SubscriptionSendStatus(
    val memberId: Long,
    val workbookId: Long,
    val sendDay: String,
    val sendTime: LocalTime,
)