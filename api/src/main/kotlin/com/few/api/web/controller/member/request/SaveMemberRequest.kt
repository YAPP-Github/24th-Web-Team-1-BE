package com.few.api.web.controller.member.request

import jakarta.validation.constraints.Email

data class SaveMemberRequest(
    @Email(message = "이메일 형식이 아닙니다.") // todo fix
    val email: String,
)