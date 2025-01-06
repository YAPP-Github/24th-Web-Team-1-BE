package com.few.api.domain.member.repo.record

import java.time.LocalDateTime

data class MemberViewRecord(
    val id: Long,
    val email: String,
    val typeCd: String,
    val description: String,
    val createdAt: LocalDateTime,
)