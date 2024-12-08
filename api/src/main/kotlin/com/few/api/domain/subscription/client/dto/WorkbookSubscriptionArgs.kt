package com.few.api.domain.subscription.client.dto

data class WorkbookSubscriptionArgs(
    val totalSubscriptions: Long,
    val activeSubscriptions: Long,
    val workbookTitle: String,
)