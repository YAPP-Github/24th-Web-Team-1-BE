package com.few.api.web.controller.image.request

import org.springframework.web.multipart.MultipartFile

data class ImageSourceRequest(
    val source: MultipartFile
)