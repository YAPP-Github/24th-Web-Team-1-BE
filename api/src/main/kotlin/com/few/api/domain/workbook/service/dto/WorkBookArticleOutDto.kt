package com.few.api.domain.workbook.service.dto

import java.net.URL
import java.time.LocalDateTime

data class WorkBookArticleOutDto(
    val articleId: Long,
    val writerId: Long,
    val mainImageURL: URL,
    val title: String,
    val category: Byte,
    val content: String,
    val createdAt: LocalDateTime,
)

data class ArticleDetailOutDto(
    val articleId: Long,
    val title: String,
)