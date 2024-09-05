package com.few.api.repo.dao.subscription.query

data class SelectAllSubscriptionSendStatusQuery(
    val memberId: Long,
    val workbookIds: List<Long>,
)