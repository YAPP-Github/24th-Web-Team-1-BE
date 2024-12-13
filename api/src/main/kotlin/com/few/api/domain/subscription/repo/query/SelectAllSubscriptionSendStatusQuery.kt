package com.few.api.domain.subscription.repo.query

data class SelectAllSubscriptionSendStatusQuery(
    val memberId: Long,
    val workbookIds: List<Long>,
)