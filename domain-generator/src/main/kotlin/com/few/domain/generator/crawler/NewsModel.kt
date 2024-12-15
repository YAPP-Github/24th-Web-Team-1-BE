package com.few.domain.generator.crawler

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class NewsModel(
    val title: String,
    val content: String,
    val date: LocalDateTime,
    val link: String,
    val originalLink: String?,
)