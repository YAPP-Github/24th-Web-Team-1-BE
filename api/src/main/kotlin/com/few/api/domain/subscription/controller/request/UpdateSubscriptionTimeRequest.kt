package com.few.api.domain.subscription.controller.request

import java.time.LocalTime

data class UpdateSubscriptionTimeRequest(
    val time: LocalTime,
    val workbookId: Long?,
)