package com.few.api.domain.subscription.usecase.dto

import java.time.LocalTime

data class UpdateSubscriptionTimeUseCaseIn(
    val memberId: Long,
    val time: LocalTime,
    val workbookId: Long?,
)