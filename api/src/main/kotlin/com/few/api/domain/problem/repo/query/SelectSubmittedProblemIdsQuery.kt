package com.few.api.domain.problem.repo.query

data class SelectSubmittedProblemIdsQuery(
    val memberId: Long,
    val problemIds: List<Long>,
)