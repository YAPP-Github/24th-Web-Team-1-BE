package com.few.api.domain.problem.repo.support

data class Contents(
    val contents: List<Content>,
)

data class Content(
    val number: Long,
    val content: String,
)