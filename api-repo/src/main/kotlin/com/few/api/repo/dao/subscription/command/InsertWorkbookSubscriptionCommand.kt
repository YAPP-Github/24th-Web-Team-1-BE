package com.few.api.repo.dao.subscription.command

import com.few.data.common.code.DayCode
import java.time.LocalTime

data class InsertWorkbookSubscriptionCommand(
    val workbookId: Long,
    val memberId: Long,
    val sendDay: String? = DayCode.MON_TUE_WED_THU_FRI.code,
    val sendTime: LocalTime? = LocalTime.of(8, 0),
)