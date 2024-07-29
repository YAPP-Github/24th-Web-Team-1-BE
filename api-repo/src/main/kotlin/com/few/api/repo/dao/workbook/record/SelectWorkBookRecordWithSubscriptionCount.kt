package com.few.api.repo.dao.workbook.record

import java.net.URL
import java.time.LocalDateTime

data class SelectWorkBookRecordWithSubscriptionCount(
    val id: Long,
    val title: String,
    val mainImageUrl: URL,
    val category: Byte,
    val description: String,
    val createdAt: LocalDateTime,
    val subscriptionCount: Long,
)