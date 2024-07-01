package com.few.api.web.controller.admin.request

import org.springframework.web.multipart.MultipartFile

data class ConvertContentRequest(
    val content: MultipartFile
)