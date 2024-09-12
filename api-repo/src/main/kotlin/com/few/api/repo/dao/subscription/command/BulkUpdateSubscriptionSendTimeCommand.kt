package com.few.api.repo.dao.subscription.command

import java.time.LocalTime

data class BulkUpdateSubscriptionSendTimeCommand(
    val memberId: Long,
    val time: LocalTime,
    val workbookIds: List<Long>,
)