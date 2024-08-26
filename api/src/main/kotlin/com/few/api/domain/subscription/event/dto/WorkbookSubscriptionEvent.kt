package com.few.api.domain.subscription.event.dto

data class WorkbookSubscriptionEvent(
    val memberId: Long,
    val workbookId: Long,
    val articleDayCol: Int,
)