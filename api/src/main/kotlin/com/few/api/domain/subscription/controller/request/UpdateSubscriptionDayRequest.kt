package com.few.api.domain.subscription.controller.request

import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateSubscriptionDayRequest(
    @JsonProperty("date")
    val dayCode: String,
    val workbookId: Long?,
)