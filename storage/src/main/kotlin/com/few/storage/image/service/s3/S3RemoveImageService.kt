package com.few.storage.image.service.s3

import com.few.storage.image.client.ImageStoreClient
import com.few.storage.image.client.util.ImageArgsGenerator
import com.few.image.service.RemoveImageService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class S3RemoveImageService(
    @Value("\${image.store.bucket-name}") val bucket: String,
    private val imageStoreClient: ImageStoreClient
) : RemoveImageService {
    override fun execute(image: String): Boolean {
        ImageArgsGenerator.remove(bucket, image).let { args ->
            return imageStoreClient.removeObject(args)
        }
    }
}