package com.few.storage.image.service.s3

import com.few.storage.image.client.ImageStoreClient
import com.few.storage.image.client.util.ImageArgsGenerator
import com.few.storage.image.service.GetPreSignedImageUrlService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class S3GetPreSignedImageUrlService(
    @Value("\${image.store.bucket-name}") val bucket: String,
    private val imageStoreClient: ImageStoreClient,
) : GetPreSignedImageUrlService {
    override fun execute(image: String): String? {
        ImageArgsGenerator.preSignedUrl(bucket, image).let { args ->
            return imageStoreClient.getPreSignedObjectUrl(args)
        }
    }
}