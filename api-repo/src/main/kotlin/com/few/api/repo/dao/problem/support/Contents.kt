package com.few.api.repo.dao.problem.support

data class Contents(
    val contents: List<Content>
)

data class Content(
    val number: Long,
    val content: String
)