package com.few.api.web.controller.article.response

import com.fasterxml.jackson.annotation.JsonFormat
import java.net.URL
import java.time.LocalDateTime

data class ReadArticleResponse(
    val id: Long,
    val writer: WriterInfo,
    val mainImageUrl: URL,
    val title: String,
    val content: String,
    val problemIds: List<Long>,
    val category: String,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val createdAt: LocalDateTime,
    val views: Long,
    val workbooks: List<WorkbookInfo> = emptyList(),
)

data class WriterInfo(
    val id: Long,
    val name: String,
    val url: URL,
    val imageUrl: URL,
)

data class WorkbookInfo(
    val id: Long,
    val title: String,
)