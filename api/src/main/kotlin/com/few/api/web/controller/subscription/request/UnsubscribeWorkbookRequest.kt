package com.few.api.web.controller.subscription.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class UnsubscribeWorkbookRequest(
    @field:NotBlank(message = "Email must not be blank")
    @field:Email(message = "Email should be valid")
    val email: String,
    val opinion: String
)