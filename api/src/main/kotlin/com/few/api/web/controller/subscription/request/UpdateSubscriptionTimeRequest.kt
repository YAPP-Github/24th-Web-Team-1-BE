package com.few.api.web.controller.subscription.request

import java.time.LocalTime

data class UpdateSubscriptionTimeRequest(
    val time: LocalTime,
    val workbookId: Long?,
)