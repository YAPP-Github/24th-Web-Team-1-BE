package com.few.api.domain.problem.repo.query

data class SelectProblemIdByArticleIdsQuery(
    val articleIds: Set<Long>,
)