package com.few.api.domain.batch.article.writer.model

import com.few.api.domain.batch.article.dto.WorkBookSubscriberItem

/**
 * 학습지의 마지막 아티클을 받은 구독자 정보 필터
 */
class ReceiveLastArticleRecordFilter(
    private val items: List<WorkBookSubscriberItem>,
    private val workbooksMappedLastDayCol: Map<Long, Int>,
) {

    fun filter(): List<WorkBookSubscriberItem> {
        return items.filter {
            it.targetWorkBookId in workbooksMappedLastDayCol.keys
        }.filter {
            (it.progress.toInt() + 1) == workbooksMappedLastDayCol[it.targetWorkBookId]
        }
    }
}