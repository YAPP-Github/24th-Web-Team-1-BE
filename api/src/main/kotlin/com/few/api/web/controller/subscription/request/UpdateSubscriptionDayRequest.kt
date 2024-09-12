package com.few.api.web.controller.subscription.request

import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateSubscriptionDayRequest(
    @JsonProperty("date")
    val dayCode: String,
    val workbookId: Long?,
)