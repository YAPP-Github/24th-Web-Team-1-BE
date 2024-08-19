package com.few.batch.service.article.writer.model

import com.few.batch.service.article.dto.WorkBookSubscriberItem
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.stream.IntStream

class ReceiveLastArticleRecordFilterTest {

    @Test
    fun `학습지의 마지막 아티클을 받은 구독자 정보를 필터링 한다`() {
        // given
        val targetWorkbookId = 1L
        val subscriberItemCount = 5
        // 가장 큰 아이디를 가진 구독자는 마지막 아티클을 받은 상태
        val items = IntStream.range(1, 1 + subscriberItemCount).mapToObj {
            WorkBookSubscriberItem(it.toLong(), targetWorkbookId, it.toLong() - 1)
        }.toList()
        val workbooksMappedLastDayCol = mapOf(1L to subscriberItemCount)

        val filter = ReceiveLastArticleRecordFilter(items, workbooksMappedLastDayCol)

        // when
        val result = filter.filter()

        // then
        assertEquals(1, result.size)
        assertEquals(subscriberItemCount.toLong(), result[0].memberId)
        assertEquals(targetWorkbookId, result[0].targetWorkBookId)
    }
}