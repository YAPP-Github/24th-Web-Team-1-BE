package com.few.api.domain.subscription.repo.query

/**
 * 멤버의 구독 완료 워크북 목록을 조회합니다.
 */
data class SelectAllMemberWorkbookInActiveSubscriptionQuery(
    val memberId: Long,
    val unsubOpinion: String = "receive.all",
)