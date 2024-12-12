package com.few.api.domain.admin.controller.response

import java.net.URL

data class ConvertContentResponse(
    val content: String,
    val originDownLoadUrl: URL,
)