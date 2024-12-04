package com.few.api.domain.member.repo.record

import java.net.URL

data class WriterRecord(
    val writerId: Long,
    val name: String,
    val url: URL,
    val imageUrl: URL,
)