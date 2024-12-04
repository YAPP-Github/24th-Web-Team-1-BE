package com.few.api.domain.subscription.repo.record

data class CountAllSubscriptionStatusRecord(
    val totalSubscriptions: Long,
    val activeSubscriptions: Long,
)