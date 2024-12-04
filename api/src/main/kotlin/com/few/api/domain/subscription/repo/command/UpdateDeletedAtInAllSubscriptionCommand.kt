package com.few.api.domain.subscription.repo.command

data class UpdateDeletedAtInAllSubscriptionCommand(
    val memberId: Long,
    val opinion: String,
)