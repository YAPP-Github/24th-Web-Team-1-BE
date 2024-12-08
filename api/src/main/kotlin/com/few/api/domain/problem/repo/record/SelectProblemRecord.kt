package com.few.api.domain.problem.repo.record

data class SelectProblemRecord(
    val id: Long,
    val title: String,
    val contents: String,
    val articleId: Long,
)