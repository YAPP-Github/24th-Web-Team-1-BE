package com.few.api.domain.problem.controller.response

data class BrowseProblemsResponse(
    val problemIds: List<Long>,
    val size: Int? = null,
)