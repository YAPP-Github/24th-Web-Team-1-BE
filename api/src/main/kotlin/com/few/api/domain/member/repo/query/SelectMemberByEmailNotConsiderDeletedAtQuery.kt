package com.few.api.domain.member.repo.query

data class SelectMemberByEmailNotConsiderDeletedAtQuery(
    val email: String,
)