package com.few.api.repo.dao.subscription.query

/**
 * 멤버의 구독 중인 워크북 목록을 조회합니다.
 */
data class SelectAllMemberWorkbookActiveSubscription(
    val memberId: Long,
)