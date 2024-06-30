package com.few.storage.image.service

import com.few.storage.image.client.dto.ImageWriteResponse
import java.io.File

fun interface PutImageService {
    fun execute(name: String, file: File): ImageWriteResponse?
}