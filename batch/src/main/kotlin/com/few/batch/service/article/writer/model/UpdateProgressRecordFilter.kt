package com.few.batch.service.article.writer.model

import com.few.batch.service.article.dto.WorkBookSubscriberItem

/**
 * 진행률을 업데이트할 구독자 정보 필터
 * - 학습지를 성공적으로 받은 구독자만 진행률을 업데이트한다.
 * - 학습지의 마지막 아티클을 받은 구독자는 진행률을 업데이트하지 않고 구독을 해지한다.
 */
class UpdateProgressRecordFilter(
    private val items: List<WorkBookSubscriberItem>,
    private val successMemberIds: Set<Long>,
    private val receiveLastDayArticleRecordMemberIds: Set<Long>,
) {
    fun filter(): List<WorkBookSubscriberItem> {
        return items.filter { it.memberId in successMemberIds }.filterNot { it.memberId in receiveLastDayArticleRecordMemberIds }
    }
}