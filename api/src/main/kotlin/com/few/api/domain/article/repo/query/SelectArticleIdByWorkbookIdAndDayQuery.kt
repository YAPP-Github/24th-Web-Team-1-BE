package com.few.api.domain.article.repo.query

data class SelectArticleIdByWorkbookIdAndDayQuery(
    val workbookId: Long,
    val day: Int,
)