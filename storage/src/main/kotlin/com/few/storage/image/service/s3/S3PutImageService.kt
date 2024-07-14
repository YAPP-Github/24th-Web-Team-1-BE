package com.few.storage.image.service.s3

import com.few.storage.image.client.ImageStoreClient
import com.few.storage.image.client.dto.ImageWriteResponse
import com.few.storage.image.client.util.ImageArgsGenerator
import com.few.storage.image.service.PutImageService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File

@Service
class S3PutImageService(
    @Value("\${image.store.bucket-name}") val bucket: String,
    private val imageStoreClient: ImageStoreClient,
) : PutImageService {
    override fun execute(name: String, file: File): ImageWriteResponse? {
        ImageArgsGenerator.putImage(bucket, name, file).let { args ->
            return imageStoreClient.putObject(args)
        }
    }
}