package com.few.api.domain.workbook.usecase.dto

import java.net.URL
import java.time.LocalDateTime

data class ReadWorkbookUseCaseOut(
    val id: Long,
    val mainImageUrl: URL,
    val title: String,
    val description: String,
    val category: String,
    val createdAt: LocalDateTime,
    val writers: List<WriterDetail>,
    val articles: List<ArticleDetail>,
)

data class WriterDetail(
    val id: Long,
    val name: String,
    val url: URL,
)

data class ArticleDetail(
    val id: Long,
    val title: String,
)