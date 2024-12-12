package com.few.api.domain.subscription.usecase.dto

import com.few.api.domain.common.vo.DayCode

data class UpdateSubscriptionDayUseCaseIn(
    val memberId: Long,
    val dayCode: DayCode,
    val workbookId: Long?,
)