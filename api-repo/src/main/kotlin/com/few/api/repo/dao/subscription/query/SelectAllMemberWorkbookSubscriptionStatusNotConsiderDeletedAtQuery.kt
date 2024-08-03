package com.few.api.repo.dao.subscription.query

/**
 * DeleteAt을 고려하지 않고 멤버의 모든 워크북 구독 상태를 조회하는 쿼리
 */
data class SelectAllMemberWorkbookSubscriptionStatusNotConsiderDeletedAtQuery(
    val memberId: Long,
)