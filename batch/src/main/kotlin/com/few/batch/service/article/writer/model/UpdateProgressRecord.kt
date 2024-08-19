package com.few.batch.service.article.writer.model

data class UpdateProgressRecord(
    val memberId: Long,
    val targetWorkBookId: Long,
    val progress: Long,
) {
    val updatedProgress: Long = progress + 1
}