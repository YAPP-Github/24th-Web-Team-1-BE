package com.few.api.domain.problem.repo.command

data class InsertSubmitHistoryCommand(
    val problemId: Long,
    val memberId: Long,
    val submitAns: String,
    val isSolved: Boolean,
)