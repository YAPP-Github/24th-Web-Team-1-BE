package com.few.api.domain.subscription.usecase.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.few.api.web.support.DayCode
import com.few.api.web.support.WorkBookStatus
import java.time.LocalTime

data class BrowseSubscribeWorkbooksUseCaseOut(
    val workbooks: List<SubscribeWorkbookDetail>,
    val clazz: Class<out SubscribeWorkbookDetail>,
)

open class SubscribeWorkbookDetail(
    val workbookId: Long,
    val isActiveSub: WorkBookStatus,
    val currentDay: Int,
    val totalDay: Int,
    val rank: Long = 0,
    val totalSubscriber: Long,
    val subscription: Subscription,
)

data class Subscription(
    @JsonFormat(pattern = "HH:mm")
    val time: LocalTime = LocalTime.of(0, 0),
    val dateTimeCode: String = DayCode.MON_TUE_WED_THU_FRI_SAT_SUN.code,
)

class MainCardSubscribeWorkbookDetail(
    workbookId: Long,
    isActiveSub: WorkBookStatus,
    currentDay: Int,
    totalDay: Int,
    rank: Long = 0,
    totalSubscriber: Long,
    subscription: Subscription,
    val articleInfo: String = "{}",
) : SubscribeWorkbookDetail(
    workbookId = workbookId,
    isActiveSub = isActiveSub,
    currentDay = currentDay,
    totalDay = totalDay,
    rank = rank,
    totalSubscriber = totalSubscriber,
    subscription = subscription
)

class MyPageSubscribeWorkbookDetail(
    workbookId: Long,
    isActiveSub: WorkBookStatus,
    currentDay: Int,
    totalDay: Int,
    rank: Long = 0,
    totalSubscriber: Long,
    subscription: Subscription,
    val workbookInfo: String = "{}",
) : SubscribeWorkbookDetail(
    workbookId = workbookId,
    isActiveSub = isActiveSub,
    currentDay = currentDay,
    totalDay = totalDay,
    rank = rank,
    totalSubscriber = totalSubscriber,
    subscription = subscription
)