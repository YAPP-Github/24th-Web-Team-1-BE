package com.few.api.domain.admin.controller.response

import java.net.URL

data class ImageSourceResponse(
    val url: URL,
    val supportSuffix: List<String>,
)