package com.few.api.repo.dao.member.record

import java.net.URL

data class WriterRecord(
    val writerId: Long,
    val name: String,
    val url: URL,
)