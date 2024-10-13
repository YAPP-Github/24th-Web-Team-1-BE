package com.few.api.web.controller.problem.response

data class BrowseProblemsResponse(
    val problemIds: List<Long>,
    val size: Int? = null,
)