package com.few.api.domain.workbook.controller.response

import java.net.URL
import java.time.LocalDateTime

data class BrowseWorkBooksResponse(
    val workbooks: List<BrowseWorkBookInfo>,
)

data class BrowseWorkBookInfo(
    val id: Long,
    val mainImageUrl: URL,
    val title: String,
    val description: String,
    val category: String,
    val createdAt: LocalDateTime,
    val writers: List<WriterInfo>,
    val subscriberCount: Long,
)