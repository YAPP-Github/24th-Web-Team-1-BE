package com.few.api.domain.article.service.dto

import java.net.URL

data class ReadWriterOutDto(
    val writerId: Long,
    val name: String,
    val url: URL,
    val imageUrl: URL,
)