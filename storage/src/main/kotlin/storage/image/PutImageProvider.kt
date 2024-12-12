package storage.image

import storage.PutObjectProvider
import storage.image.client.dto.ImageWriteResponse
import java.io.File

fun interface PutImageProvider : PutObjectProvider<ImageWriteResponse> {
    override fun execute(name: String, file: File): ImageWriteResponse?
}