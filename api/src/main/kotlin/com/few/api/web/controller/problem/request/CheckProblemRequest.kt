package com.few.api.web.controller.problem.request

import jakarta.validation.constraints.NotBlank

data class CheckProblemRequest(
    @field:NotBlank(message = "{sub.notblank}")
    val sub: String,
)