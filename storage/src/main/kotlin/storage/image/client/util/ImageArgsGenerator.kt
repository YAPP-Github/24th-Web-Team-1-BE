package storage.image.client.util

import org.apache.http.entity.ContentType
import storage.image.client.dto.ImageGetPreSignedObjectUrlArgs
import storage.image.client.dto.ImagePutObjectArgs
import storage.image.client.dto.ImageRemoveObjectArgs
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream

class ImageArgsGenerator {
    companion object {
        fun preSignedUrl(
            bucket: String,
            image: String,
        ): ImageGetPreSignedObjectUrlArgs = ImageGetPreSignedObjectUrlArgs(bucket, image, "GET")

        fun putImage(
            bucket: String,
            name: String,
            image: File,
        ): ImagePutObjectArgs {
            val contentType =
                image.extension.let { ext ->
                    when (ext) {
                        "jpg", "jpeg" -> ContentType.IMAGE_JPEG
                        "png" -> ContentType.IMAGE_PNG
                        "gif" -> ContentType.IMAGE_GIF
                        "svg" -> ContentType.IMAGE_SVG
                        "webp" -> ContentType.IMAGE_WEBP
                        else -> throw IllegalArgumentException("Unsupported image type: $ext")
                    }
                }
            return ImagePutObjectArgs(bucket, name, BufferedInputStream(FileInputStream(image)), image.length(), -1, contentType)
        }

        fun remove(
            bucket: String,
            image: String,
        ): ImageRemoveObjectArgs = ImageRemoveObjectArgs(bucket, image)
    }
}