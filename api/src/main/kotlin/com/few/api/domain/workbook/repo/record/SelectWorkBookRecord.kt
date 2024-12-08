package com.few.api.domain.workbook.repo.record

import java.net.URL
import java.time.LocalDateTime

data class SelectWorkBookRecord(
    val id: Long,
    val title: String,
    val mainImageUrl: URL,
    val category: Byte,
    val description: String,
    val createdAt: LocalDateTime,
)