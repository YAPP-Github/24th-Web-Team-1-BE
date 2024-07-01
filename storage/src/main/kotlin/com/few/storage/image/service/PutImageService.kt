package com.few.storage.image.service

import com.few.storage.PutObjectService
import com.few.storage.image.client.dto.ImageWriteResponse
import java.io.File

fun interface PutImageService : PutObjectService<ImageWriteResponse> {
    override fun execute(name: String, file: File): ImageWriteResponse?
}