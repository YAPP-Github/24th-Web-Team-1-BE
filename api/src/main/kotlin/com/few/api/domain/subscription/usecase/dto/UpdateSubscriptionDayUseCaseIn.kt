package com.few.api.domain.subscription.usecase.dto

import com.few.api.web.support.DayCode

data class UpdateSubscriptionDayUseCaseIn(
    val memberId: Long,
    val dayCode: DayCode,
    val workbookId: Long?,
)