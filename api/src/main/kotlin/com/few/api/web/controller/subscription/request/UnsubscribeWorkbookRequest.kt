package com.few.api.web.controller.subscription.request

data class UnsubscribeWorkbookRequest(
    val email: String,
    val opinion: String
)