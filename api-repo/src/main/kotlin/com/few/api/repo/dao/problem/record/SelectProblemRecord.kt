package com.few.api.repo.dao.problem.record

data class SelectProblemRecord(
    val id: Long,
    val title: String,
    val contents: String // TODO: convert json type
)