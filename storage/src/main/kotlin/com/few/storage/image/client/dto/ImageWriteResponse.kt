package com.few.storage.image.client.dto

data class ImageWriteResponse(
    val bucket: String,
    val region: String,
    val `object`: String,
    val etag: String,
    val versionId: String
)