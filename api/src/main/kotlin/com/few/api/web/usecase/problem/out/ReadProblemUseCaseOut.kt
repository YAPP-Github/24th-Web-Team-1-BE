package com.few.api.web.usecase.problem.out

class ReadProblemUseCaseOut(
    val id: Long,
    val title: String,
    val contents: List<ReadProblemContentsUseCaseOut>
)

data class ReadProblemContentsUseCaseOut(
    val number: Long,
    val content: String
)