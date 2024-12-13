package com.few.api.domain.article.repo.query

data class SelectAritlceIdByWorkbookIdAndDayQuery(
    val workbookId: Long,
    val day: Int,
)