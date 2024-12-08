package com.few.api.domain.problem.controller.response

data class CheckProblemResponse(
    val explanation: String,
    val answer: String,
    val isSolved: Boolean,
)