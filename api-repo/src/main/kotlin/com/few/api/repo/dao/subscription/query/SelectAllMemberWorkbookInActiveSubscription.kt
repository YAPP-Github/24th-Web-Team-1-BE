package com.few.api.repo.dao.subscription.query

/**
 * 멤버의 구독 완료 워크북 목록을 조회합니다.
 */
data class SelectAllMemberWorkbookInActiveSubscription(
    val memberId: Long,
    val unsubOpinion: String = "receive.all",
)