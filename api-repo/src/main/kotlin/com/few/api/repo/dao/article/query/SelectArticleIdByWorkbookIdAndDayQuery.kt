package com.few.api.repo.dao.article.query

data class SelectArticleIdByWorkbookIdAndDayQuery(
    val workbookId: Long,
    val day: Int,
)