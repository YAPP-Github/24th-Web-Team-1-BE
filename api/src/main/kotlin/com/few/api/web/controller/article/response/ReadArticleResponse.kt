package com.few.api.web.controller.article.response

import java.net.URL
import java.time.LocalDateTime

data class ReadArticleResponse(
    val id: Long,
    val writer: WriterInfo,
    val title: String,
    val content: String,
    val problemIds: List<Long>,
    val category: String,
    val createdAt: LocalDateTime,
    val views: Long,
    val includedWorkbooks: List<WorkbookInfo>? = null,
)

data class WriterInfo(
    val id: Long,
    val name: String,
    val url: URL,
)

data class WorkbookInfo(
    val id: Long,
    val title: String,
)