package com.few.api.domain.problem.usecase.dto

data class CheckProblemUseCaseIn(
    val memberId: Long,
    val problemId: Long,
    val sub: String,
)