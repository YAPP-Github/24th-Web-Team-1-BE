package com.few.storage.image.client.util

import com.few.storage.image.client.dto.ImageGetPreSignedObjectUrlArgs
import com.few.storage.image.client.dto.ImagePutObjectArgs
import com.few.storage.image.client.dto.ImageRemoveObjectArgs
import io.minio.http.Method
import org.apache.http.entity.ContentType
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream

class ImageArgsGenerator {
    companion object {
        fun preSignedUrl(bucket: String, image: String): ImageGetPreSignedObjectUrlArgs {
            return ImageGetPreSignedObjectUrlArgs(bucket, image, Method.GET.toString())
        }

        fun putImage(bucket: String, name: String, image: File): ImagePutObjectArgs {
            // content type
            val contentType = image.extension.let { ext ->
                when (ext) {
                    "jpg", "jpeg" -> ContentType.IMAGE_JPEG
                    "png" -> ContentType.IMAGE_PNG
                    "gif" -> ContentType.IMAGE_GIF
                    else -> ContentType.APPLICATION_OCTET_STREAM
                }
            }
            return ImagePutObjectArgs(bucket, name, BufferedInputStream(FileInputStream(image)), image.length(), -1, contentType)
        }

        fun remove(bucket: String, image: String): ImageRemoveObjectArgs {
            return ImageRemoveObjectArgs(bucket, image)
        }
    }
}