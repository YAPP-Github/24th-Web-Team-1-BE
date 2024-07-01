package com.few.api.client.subscription.dto

data class WorkbookSubscriptionArgs(
    val totalSubscriptions: Long,
    val activeSubscriptions: Long,
    val workbookTitle: String
)