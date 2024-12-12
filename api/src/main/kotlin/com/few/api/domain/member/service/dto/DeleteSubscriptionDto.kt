package com.few.api.domain.member.service.dto

data class DeleteSubscriptionDto(
    val memberId: Long,
    val opinion: String = "withdrawal",
)