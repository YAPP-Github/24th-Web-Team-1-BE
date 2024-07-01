package com.few.api.web.controller.admin.response

import java.net.URL

data class ConvertContentResponse(
    val content: String,
    val originDownLoadUrl: URL
)