package com.few.api.web.controller.subscription.request

data class UpdateSubscriptionDayRequest(
    val dayCode: String,
    val workbookId: Long?,
)