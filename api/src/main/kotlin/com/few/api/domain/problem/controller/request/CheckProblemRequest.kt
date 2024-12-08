package com.few.api.domain.problem.controller.request

import jakarta.validation.constraints.NotBlank

data class CheckProblemRequest(
    @field:NotBlank(message = "{sub.notblank}")
    val sub: String,
)