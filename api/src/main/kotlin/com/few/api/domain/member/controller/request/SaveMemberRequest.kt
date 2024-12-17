package com.few.api.domain.member.controller.request

import jakarta.validation.constraints.Email

data class SaveMemberRequest(
    @Email(message = "이메일 형식이 아닙니다.")
    val email: String,
)