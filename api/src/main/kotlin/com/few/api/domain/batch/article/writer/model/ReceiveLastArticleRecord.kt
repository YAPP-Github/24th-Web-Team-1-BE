package com.few.api.domain.batch.article.writer.model

fun List<ReceiveLastArticleRecord>.getMemberIds(): Set<Long> = this.map { it.memberId }.toSet()

/** 학습지의 마지막 아티클을 받은 구독자 정보 */
data class ReceiveLastArticleRecord(
    val memberId: Long,
    val targetWorkBookId: Long,
)