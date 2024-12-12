package com.few.api.domain.subscription.repo.command

import com.few.api.domain.common.vo.DayCode
import java.time.LocalTime

data class InsertWorkbookSubscriptionCommand(
    val workbookId: Long,
    val memberId: Long,
    val sendDay: String? = DayCode.MON_TUE_WED_THU_FRI.code,
    val sendTime: LocalTime? = LocalTime.of(8, 0),
)