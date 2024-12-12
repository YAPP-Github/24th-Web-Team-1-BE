package com.few.api.domain.member.repo.support

import java.net.URL

data class WriterDescription(
    val name: String,
    val url: URL,
    val imageUrl: URL,
)