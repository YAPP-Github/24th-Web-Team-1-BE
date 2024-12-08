package com.few.api.domain.subscription.controller.response

import com.few.api.domain.subscription.usecase.dto.Subscription

data class SubscribeWorkbooksResponse(
    val workbooks: List<SubscribeWorkbookInfo>,
)

open class SubscribeWorkbookInfo(
    val id: Long,
    val status: String, // convert from enum
    val totalDay: Int,
    val currentDay: Int,
    val rank: Long,
    val totalSubscriber: Long,
    val subscription: Subscription,
)

class MainCardSubscribeWorkbookInfo(
    id: Long,
    status: String,
    totalDay: Int,
    currentDay: Int,
    rank: Long,
    totalSubscriber: Long,
    subscription: Subscription,
    val articleInfo: String,
) : SubscribeWorkbookInfo(
    id = id,
    status = status,
    totalDay = totalDay,
    currentDay = currentDay,
    rank = rank,
    totalSubscriber = totalSubscriber,
    subscription = subscription
)

class MyPageSubscribeWorkbookInfo(
    id: Long,
    status: String,
    totalDay: Int,
    currentDay: Int,
    rank: Long,
    totalSubscriber: Long,
    subscription: Subscription,
    val workbookInfo: String,
) : SubscribeWorkbookInfo(
    id = id,
    status = status,
    totalDay = totalDay,
    currentDay = currentDay,
    rank = rank,
    totalSubscriber = totalSubscriber,
    subscription = subscription
)