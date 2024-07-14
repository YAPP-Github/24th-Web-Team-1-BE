package com.few.api.web.controller.subscription.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class UnsubscribeAllRequest(
    @field:NotBlank(message = "{email.notblank}")
    @field:Email(message = "{email.invalid}")
    val email: String,
    val opinion: String,
)