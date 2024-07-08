package com.few.api.repo.dao.subscription.record

data class CountAllSubscriptionStatusRecord(
    val totalSubscriptions: Long,
    val activeSubscriptions: Long
)