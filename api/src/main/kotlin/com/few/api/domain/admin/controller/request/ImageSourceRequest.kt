package com.few.api.domain.admin.controller.request

import org.springframework.web.multipart.MultipartFile

data class ImageSourceRequest(
    val source: MultipartFile,
)