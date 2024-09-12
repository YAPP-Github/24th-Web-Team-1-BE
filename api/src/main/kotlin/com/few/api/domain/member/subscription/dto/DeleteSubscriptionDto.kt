package com.few.api.domain.member.subscription.dto

data class DeleteSubscriptionDto(
    val memberId: Long,
    val opinion: String = "withdrawal",
)