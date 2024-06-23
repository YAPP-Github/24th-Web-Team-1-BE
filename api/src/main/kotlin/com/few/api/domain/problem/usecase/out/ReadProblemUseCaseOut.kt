package com.few.api.domain.problem.usecase.out

class ReadProblemUseCaseOut(
    val id: Long,
    val title: String,
    val contents: List<ReadProblemContentsUseCaseOutDetail>
)

data class ReadProblemContentsUseCaseOutDetail(
    val number: Long,
    val content: String
)