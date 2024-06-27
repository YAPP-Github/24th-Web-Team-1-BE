package com.few.api.domain.image.dto

import org.springframework.web.multipart.MultipartFile

data class PutImageUseCaseIn(
    val source: MultipartFile
)