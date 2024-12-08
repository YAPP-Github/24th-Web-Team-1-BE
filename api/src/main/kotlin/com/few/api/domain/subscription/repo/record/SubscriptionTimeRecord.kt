package com.few.api.domain.subscription.repo.record

import java.time.LocalDateTime

data class SubscriptionTimeRecord(
    val memberId: Long,
    val workbookId: Long,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
    val sendAt: LocalDateTime?,
)