package com.few.api.domain.admin.document.usecase.dto

import org.springframework.web.multipart.MultipartFile

data class PutImageUseCaseIn(
    val source: MultipartFile,
)