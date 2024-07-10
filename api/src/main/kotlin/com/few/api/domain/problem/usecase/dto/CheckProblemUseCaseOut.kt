package com.few.api.domain.problem.usecase.dto

data class CheckProblemUseCaseOut(
    val explanation: String,
    val answer: String,
    val isSolved: Boolean
)