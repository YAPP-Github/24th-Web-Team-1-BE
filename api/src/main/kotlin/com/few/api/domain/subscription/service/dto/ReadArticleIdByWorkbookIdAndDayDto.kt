package com.few.api.domain.subscription.service.dto

data class ReadArticleIdByWorkbookIdAndDayDto(
    val workbookId: Long,
    val day: Int,
)