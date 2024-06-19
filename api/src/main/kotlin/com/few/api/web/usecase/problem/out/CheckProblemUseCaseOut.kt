package com.few.api.web.usecase.problem.out

data class CheckProblemUseCaseOut(
    val explanation: String,
    val answer: String,
    val isSolved: Boolean
)