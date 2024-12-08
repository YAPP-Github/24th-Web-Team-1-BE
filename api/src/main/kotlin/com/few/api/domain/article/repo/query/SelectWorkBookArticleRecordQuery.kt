package com.few.api.domain.article.repo.query

data class SelectWorkBookArticleRecordQuery(
    val workbookId: Long,
    val articleId: Long,
)