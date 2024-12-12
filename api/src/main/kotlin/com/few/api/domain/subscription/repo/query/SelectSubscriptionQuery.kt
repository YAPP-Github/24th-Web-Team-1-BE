package com.few.api.domain.subscription.repo.query

data class SelectSubscriptionQuery(
    val memberId: Long,
    val workbookId: Long,
)