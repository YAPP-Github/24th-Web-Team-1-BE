package com.few.api.domain.article.repo.record

import java.net.URL
import java.time.LocalDateTime

data class SelectWorkBookMappedArticleRecord(
    val articleId: Long,
    val writerId: Long,
    val mainImageURL: URL,
    val title: String,
    val category: Byte,
    val content: String,
    val createdAt: LocalDateTime,
)