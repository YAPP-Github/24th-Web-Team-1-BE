package com.few.api.repo.dao.problem.query

data class SelectProblemIdByArticleIdsQuery(
    val articleIds: Set<Long>,
)