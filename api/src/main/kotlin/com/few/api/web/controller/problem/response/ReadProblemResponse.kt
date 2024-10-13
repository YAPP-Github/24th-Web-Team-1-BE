package com.few.api.web.controller.problem.response

data class ReadProblemResponse(
    val id: Long,
    val title: String,
    val contents: List<ProblemContents>,
    val articleId: Long,
)

data class ProblemContents(
    val number: Long,
    val content: String,
)