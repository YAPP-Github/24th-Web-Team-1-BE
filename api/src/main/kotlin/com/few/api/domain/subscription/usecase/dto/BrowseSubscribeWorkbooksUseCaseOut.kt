package com.few.api.domain.subscription.usecase.dto

import com.few.api.web.support.WorkBookStatus

data class BrowseSubscribeWorkbooksUseCaseOut(
    val workbooks: List<SubscribeWorkbookDetail>,
)

data class SubscribeWorkbookDetail(
    val workbookId: Long,
    val isActiveSub: WorkBookStatus,
    val currentDay: Int,
    val totalDay: Int,
    val rank: Long = 0,
    val totalSubscriber: Long,
    val articleInfo: String = "{}",
)