package com.few.api.web.controller.workbook.article.response

import com.few.api.web.controller.workbook.response.WriterInfo
import java.time.LocalDateTime

data class ReadWorkBookArticleResponse(
    val id: Long,
    val writer: WriterInfo,
    val title: String,
    val content: String,
    val problemIds: List<Long>,
    val category: String,
    val createdAt: LocalDateTime,
    val day: Long
)