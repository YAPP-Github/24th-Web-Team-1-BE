package com.few.api.repo.dao.article.record

import java.net.URL
import java.time.LocalDateTime

data class SelectArticleRecord(
    val articleId: Long,
    val writerId: Long,
    val mainImageURL: URL,
    val title: String,
    val category: Byte,
    val content: String,
    val createdAt: LocalDateTime
)