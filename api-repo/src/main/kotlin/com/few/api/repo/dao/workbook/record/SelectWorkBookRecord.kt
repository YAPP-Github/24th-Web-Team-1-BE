package com.few.api.repo.dao.workbook.record

import java.net.URL
import java.time.LocalDateTime

data class SelectWorkBookRecord(
    val id: Long,
    val title: String,
    val mainImageUrl: URL,
    val category: Long,
    val description: String,
    val createdAt: LocalDateTime
)