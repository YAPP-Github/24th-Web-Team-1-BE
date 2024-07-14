package com.few.api.domain.workbook.service.dto

import java.net.URL

data class WriterOutDto(
    val writerId: Long,
    val name: String,
    val url: URL,
)