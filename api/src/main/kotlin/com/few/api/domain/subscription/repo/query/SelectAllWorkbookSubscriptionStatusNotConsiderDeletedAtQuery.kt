package com.few.api.domain.subscription.repo.query

/**
 * DeleteAt을 고려하지 않고 모든 워크북 구독 상태를 조회하는 쿼리
 */
data class SelectAllWorkbookSubscriptionStatusNotConsiderDeletedAtQuery(
    val workbookId: Long,
    val memberId: Long,
)