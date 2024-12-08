package com.few.api.domain.subscription.repo.command

import java.time.LocalTime

data class BulkUpdateSubscriptionSendTimeCommand(
    val memberId: Long,
    val time: LocalTime,
    val workbookIds: List<Long>,
)