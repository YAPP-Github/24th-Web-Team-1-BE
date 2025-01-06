package com.few.api.domain.member.usecase.dto

import java.time.LocalDateTime

class BrowseMemberViewsUseCaseDto

class BrowseMemberViewsUseCaseIn

data class BrowseMemberViewsUseCaseOut(
    val members: List<MemberView>,
)

data class MemberView(
    val id: Long,
    val email: String,
    val typeCd: String,
    val description: String,
    val createdAt: LocalDateTime,
)