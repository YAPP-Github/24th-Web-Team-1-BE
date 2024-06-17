package com.few.api.web.controller.problem.response

data class CheckProblemResponse(
    val explanation: String,
    val answer: String,
    val isSolved: Boolean
)