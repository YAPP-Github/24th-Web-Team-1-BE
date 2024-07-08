package com.few.storage.image.client.dto

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import org.apache.http.entity.ContentType
import java.io.InputStream

fun ImageGetPreSignedObjectUrlArgs.toS3Args(): GeneratePresignedUrlRequest {
    return GeneratePresignedUrlRequest(
        this.bucket,
        this.imagePath,
        HttpMethod.valueOf(this.method)
    )
}

fun ImageRemoveObjectArgs.toS3Args(): DeleteObjectRequest {
    return DeleteObjectRequest(this.bucket, this.imagePath)
}

fun ImagePutObjectArgs.toS3Args(): PutObjectRequest {
    val objectSize = this.objectSize
    val contentType = this.contentType.toString()
    return PutObjectRequest(
        this.bucket,
        this.imagePath,
        this.stream,
        ObjectMetadata().apply {
            this.contentType = contentType
            this.contentLength = objectSize
        }
    )
}

data class ImageGetPreSignedObjectUrlArgs(
    val bucket: String,
    val imagePath: String,
    val method: String
)

data class ImagePutObjectArgs(
    val bucket: String,
    val imagePath: String,
    val stream: InputStream,
    val objectSize: Long,
    val partSize: Long,
    val contentType: ContentType = ContentType.IMAGE_JPEG
)

data class ImageRemoveObjectArgs(
    val bucket: String,
    val imagePath: String
)