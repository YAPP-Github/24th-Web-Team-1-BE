package com.few.api.repo.dao.problem.query

data class SelectSubmittedProblemIdsQuery(
    val memberId: Long,
    val problemIds: List<Long>,
)