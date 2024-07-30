package com.few.api.domain.article.usecase.dto

import java.net.URL
import java.time.LocalDateTime

data class ReadArticleUseCaseOut(
    val id: Long,
    val writer: WriterDetail,
    val title: String,
    val content: String,
    val problemIds: List<Long>,
    val category: String,
    val createdAt: LocalDateTime,
    val views: Long,
    val workbooks: List<WorkbookDetail> = emptyList(),
)

data class WriterDetail(
    val id: Long,
    val name: String,
    val url: URL,
)

data class WorkbookDetail(
    val id: Long?,
    val title: String?,
)