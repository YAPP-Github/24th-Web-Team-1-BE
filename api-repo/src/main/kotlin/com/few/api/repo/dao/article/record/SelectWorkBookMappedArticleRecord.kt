package com.few.api.repo.dao.article.record

import java.time.LocalDateTime

data class SelectWorkBookMappedArticleRecord(
    val articleId: Long,
    val writerId: Long,
    val mainImageURL: String,
    val title: String,
    val category: Byte,
    val content: String,
    val createdAt: LocalDateTime
)