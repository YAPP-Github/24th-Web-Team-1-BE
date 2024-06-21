package com.few.api.domain.workbook.article.dto

import java.net.URL
import java.time.LocalDateTime

data class ReadWorkBookArticleOut(
    val id: Long,
    val writer: WriterDetail,
    val title: String,
    val content: String,
    val problemIds: List<Long>,
    val category: String,
    val createdAt: LocalDateTime,
    val day: Long
)

data class WriterDetail(
    val id: Long,
    val name: String,
    val url: URL
)