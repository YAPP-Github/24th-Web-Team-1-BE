package com.few.api.domain.admin.usecase.dto

import java.net.URL

data class ConvertContentUseCaseOut(
    val content: String,
    val originDownLoadUrl: URL,
)