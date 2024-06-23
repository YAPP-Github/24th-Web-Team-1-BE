package com.few.api.repo.dao.subscription.command

data class UpdateDeletedAtInAllSubscriptionCommand(
    val memberId: Long,
    val opinion: String
)