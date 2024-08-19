package com.few.batch.service.article.writer.model

import com.few.batch.service.article.dto.WorkBookSubscriberItem
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.stream.Collectors.toSet
import java.util.stream.IntStream

class UpdateProgressRecordFilterTest {

    @Test
    fun `진행률을 업데이트할 구독자 정보를 필터링한다`() {
        // given
        val targetWorkbookId = 1L
        val subscriberItemCount = 5
        // 가장 큰 아이디를 가진 구독자는 마지막 아티클을 받은 상태
        val successMemberIds =
            IntStream.range(1, 1 + subscriberItemCount).mapToObj { it.toLong() }.collect(toSet())
        val receiveLastDayArticleRecordMemberIds = setOf(subscriberItemCount.toLong())
        val items = (1..subscriberItemCount).map {
            WorkBookSubscriberItem(it.toLong(), targetWorkbookId, it.toLong() - 1)
        }

        val filter = UpdateProgressRecordFilter(items, successMemberIds, receiveLastDayArticleRecordMemberIds)

        // when
        val result = filter.filter()

        // then
        assertEquals(4, result.size)
        result.forEach {
            assertTrue(it.memberId in successMemberIds)
            assertFalse(it.memberId in receiveLastDayArticleRecordMemberIds)
        }
    }
}