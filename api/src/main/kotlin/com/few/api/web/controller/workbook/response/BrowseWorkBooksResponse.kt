package com.few.api.web.controller.workbook.response

import com.fasterxml.jackson.annotation.JsonFormat
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val createdAt: LocalDateTime,
    val writers: List<WriterInfo>,
    val subscriberCount: Long,
)