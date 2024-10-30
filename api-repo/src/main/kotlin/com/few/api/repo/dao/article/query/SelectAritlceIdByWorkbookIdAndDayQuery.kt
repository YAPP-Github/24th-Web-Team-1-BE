package com.few.api.repo.dao.article.query

data class SelectAritlceIdByWorkbookIdAndDayQuery(
    val workbookId: Long,
    val day: Int,
)