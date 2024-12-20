package com.few.api.domain.admin.usecase.dto

import java.net.URL

data class PutImageUseCaseOut(
    val url: URL,
    val supportSuffix: List<String>,
)