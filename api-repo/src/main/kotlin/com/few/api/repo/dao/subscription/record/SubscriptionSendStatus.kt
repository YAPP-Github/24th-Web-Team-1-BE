package com.few.api.repo.dao.subscription.record

import java.time.LocalTime

data class SubscriptionSendStatus(
    val memberId: Long,
    val workbookId: Long,
    val sendDay: String,
    val sendTime: LocalTime,
)