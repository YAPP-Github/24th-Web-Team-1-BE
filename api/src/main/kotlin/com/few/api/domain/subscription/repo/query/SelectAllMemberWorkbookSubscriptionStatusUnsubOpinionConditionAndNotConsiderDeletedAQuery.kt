package com.few.api.domain.subscription.repo.query

/**
 * UnsubOpinion 조건을 만족하고
 * DeleteAt을 고려하지 않고 멤버의 모든 워크북 구독 상태를 조회하는 쿼리
 */
data class SelectAllMemberWorkbookSubscriptionStatusUnsubOpinionConditionAndNotConsiderDeletedAQuery(
    val memberId: Long,
    val unsubOpinion: String = "receive.all",
    val activeSubscriptionUnsubOpinion: String = "",
)