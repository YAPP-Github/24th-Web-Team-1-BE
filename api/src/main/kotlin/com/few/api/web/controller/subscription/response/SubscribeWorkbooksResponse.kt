package com.few.api.web.controller.subscription.response

data class SubscribeWorkbooksResponse(
    val workbooks: List<SubscribeWorkbookInfo>,
)

data class SubscribeWorkbookInfo(
    val id: Long,
    val status: String, // convert from enum
    val totalDay: Int,
    val currentDay: Int,
    val rank: Long,
    val totalSubscriber: Long,
    val articleInfo: String, // convert from Json
)