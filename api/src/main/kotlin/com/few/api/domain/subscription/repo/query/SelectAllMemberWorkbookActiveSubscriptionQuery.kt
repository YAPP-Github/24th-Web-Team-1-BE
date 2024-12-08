package com.few.api.domain.subscription.repo.query

/**
 * 멤버의 구독 중인 워크북 목록을 조회합니다.
 */
data class SelectAllMemberWorkbookActiveSubscriptionQuery(
    val memberId: Long,
)