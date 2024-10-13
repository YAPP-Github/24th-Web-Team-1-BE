package com.few.api.domain.problem.usecase.dto

class ReadProblemUseCaseOut(
    val id: Long,
    val title: String,
    val contents: List<ReadProblemContentsUseCaseOutDetail>,
    val articleId: Long,
)

data class ReadProblemContentsUseCaseOutDetail(
    val number: Long,
    val content: String,
)