package com.few.api.domain.problem.usecase.dto

data class CheckProblemUseCaseIn(
    val problemId: Long,
    val sub: String,
)