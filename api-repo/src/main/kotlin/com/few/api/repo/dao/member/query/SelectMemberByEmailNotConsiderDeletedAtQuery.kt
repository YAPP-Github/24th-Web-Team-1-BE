package com.few.api.repo.dao.member.query

data class SelectMemberByEmailNotConsiderDeletedAtQuery(
    val email: String,
)