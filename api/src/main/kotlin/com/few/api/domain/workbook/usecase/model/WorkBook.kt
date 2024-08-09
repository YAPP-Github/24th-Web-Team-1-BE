package com.few.api.domain.workbook.usecase.model

import java.net.URL
import java.time.LocalDateTime

data class WorkBook(
    val id: Long,
    val mainImageUrl: URL,
    val title: String,
    val description: String,
    val category: String,
    val createdAt: LocalDateTime,
    val writerDetails: List<WorkBookWriter>,
    val subscriptionCount: Long,
)

data class WorkBookWriter(
    val id: Long,
    val name: String,
    val url: URL,
)