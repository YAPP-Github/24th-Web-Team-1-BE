package com.few.api.domain.admin.document.dto

import org.springframework.web.multipart.MultipartFile

data class ConvertContentUseCaseIn(
    val content: MultipartFile
)