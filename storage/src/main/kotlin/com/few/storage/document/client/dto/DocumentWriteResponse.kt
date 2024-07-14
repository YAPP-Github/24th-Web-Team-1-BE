package com.few.storage.document.client.dto

data class DocumentWriteResponse(
    val bucket: String,
    val region: String,
    val `object`: String,
    val etag: String,
    val versionId: String,
)