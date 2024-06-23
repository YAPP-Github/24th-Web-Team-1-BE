package com.few.api.domain.problem.usecase.out

data class CheckProblemUseCaseOut(
    val explanation: String,
    val answer: String,
    val isSolved: Boolean
)