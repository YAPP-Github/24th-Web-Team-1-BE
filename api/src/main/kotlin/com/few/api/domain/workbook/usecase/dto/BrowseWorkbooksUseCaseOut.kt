package com.few.api.domain.workbook.usecase.dto

import java.net.URL
import java.time.LocalDateTime

data class BrowseWorkbooksUseCaseOut(
    val workbooks: List<BrowseWorkBookDetail>,
)

data class BrowseWorkBookDetail(
    val id: Long,
    val mainImageUrl: URL,
    val title: String,
    val description: String,
    val category: String,
    val createdAt: LocalDateTime,
    val writerDetails: List<WriterDetail>,
    val subscriptionCount: Long,
)