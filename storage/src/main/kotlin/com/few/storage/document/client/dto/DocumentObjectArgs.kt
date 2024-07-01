package com.few.storage.document.client.dto

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import org.apache.http.entity.ContentType
import java.io.InputStream

fun DocumentGetPreSignedObjectUrlArgs.toS3Args(): GeneratePresignedUrlRequest {
    return GeneratePresignedUrlRequest(
        this.bucket,
        this.imagePath,
        HttpMethod.valueOf(this.method)
    )
}

fun DocumentRemoveObjectArgs.toS3Args(): DeleteObjectRequest {
    return DeleteObjectRequest(this.bucket, this.imagePath)
}

fun DocumentPutObjectArgs.toS3Args(): PutObjectRequest {
    val objectSize = this.objectSize
    return PutObjectRequest(
        this.bucket,
        this.imagePath,
        this.stream,
        ObjectMetadata().apply {
            contentType = this.contentType
            contentLength = objectSize
        }
    )
}

data class DocumentGetPreSignedObjectUrlArgs(
    val bucket: String,
    val imagePath: String,
    val method: String
)

data class DocumentPutObjectArgs(
    val bucket: String,
    val imagePath: String,
    val stream: InputStream,
    val objectSize: Long,
    val partSize: Long,
    val contentType: ContentType = ContentType.IMAGE_JPEG
)

data class DocumentRemoveObjectArgs(
    val bucket: String,
    val imagePath: String
)