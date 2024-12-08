package com.few.api.domain.admin.controller.request

import org.springframework.web.multipart.MultipartFile

data class ConvertContentRequest(
    val content: MultipartFile,
)