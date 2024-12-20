package com.few.api.domain.workbook.usecase.model.order

import com.few.api.domain.workbook.usecase.model.MemberSubscribedWorkbook
import com.few.api.domain.workbook.usecase.model.WorkBook
import com.few.api.domain.workbook.usecase.model.WorkBooks
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.net.URL
import java.time.LocalDateTime
import java.util.stream.IntStream
import kotlin.streams.toList

@Epic("V1.0 Model")
@Feature("AuthMainViewWorkbookOrderDelegator")
class AuthMainViewWorkbookOrderDelegatorTest {
    @Test
    fun `워크북과 멤버 구독 워크북이 모두 주어지는 경우`() {
        // given
        val totalWorkbookCount = 10
        val workbooksOrderBySubscriptionCount =
            IntStream
                .range(1, 1 + totalWorkbookCount)
                .mapToObj {
                    WorkBook(
                        it.toLong(),
                        URL("http://localhost:8080/$it"),
                        "title$it",
                        "description$it",
                        "category$it",
                        LocalDateTime.now(),
                        emptyList(),
                        (1 + totalWorkbookCount) - it.toLong(),
                    )
                }.toList()

        /**
         * 1 : inactive, current day 5
         * 2 : active, current day 4
         * 3 : inactive, current day 3
         * 4 : active, current day 2
         * 5 : inactive, current day 1
         */
        val activeWorkbookIds = listOf(2, 4)
        val inActiveList = listOf(1, 3, 5)
        val totalMemberSubscribedWorkbookCount = 5
        val memberSubscribedWorkbooksReverseOrderByCurrentDay =
            IntStream
                .range(1, 1 + totalMemberSubscribedWorkbookCount)
                .mapToObj {
                    MemberSubscribedWorkbook(
                        it.toLong(),
                        it % 2 == 0,
                        (1 + totalMemberSubscribedWorkbookCount) - it,
                    )
                }.toList()

        val notSubscribeWorkbookIds =
            workbooksOrderBySubscriptionCount
                .filter {
                    !activeWorkbookIds.contains(it.id.toInt()) &&
                        !inActiveList.contains(it.id.toInt())
                }.map { it.id.toInt() }

        // when
        val delegator =
            AuthMainViewWorkbookOrderDelegator(
                memberSubscribedWorkbooksReverseOrderByCurrentDay,
            )

        // then
        val orderedWorkbooks = delegator.order(OrderTargetWorkBooks(WorkBooks(workbooksOrderBySubscriptionCount)))
        assertEquals(totalWorkbookCount, orderedWorkbooks.workbooks.workbookData.size)

        val expectedOrderedWorkbookIds = activeWorkbookIds + notSubscribeWorkbookIds + inActiveList
        for (i in expectedOrderedWorkbookIds.indices) {
            assertEquals(expectedOrderedWorkbookIds[i].toLong(), orderedWorkbooks.workbooks.workbookData[i].id)
        }
    }

    @Test
    fun `워크북과 멤버 구독 워크북만 주어지는 경우`() {
        // given
        val totalWorkbookCount = 10
        val workbooksOrderBySubscriptionCount =
            IntStream
                .range(1, 1 + totalWorkbookCount)
                .mapToObj {
                    WorkBook(
                        it.toLong(),
                        URL("http://localhost:8080/$it"),
                        "title$it",
                        "description$it",
                        "category$it",
                        LocalDateTime.now(),
                        emptyList(),
                        (1 + totalWorkbookCount) - it.toLong(),
                    )
                }.toList()

        /**
         * 1 : active, current day 5
         * 2 : active, current day 4
         * 3 : active, current day 3
         * 4 : active, current day 2
         * 5 : active, current day 1
         */
        val totalMemberSubscribedWorkbookCount = 5
        val activeWorkbookIds = IntStream.range(1, 1 + totalMemberSubscribedWorkbookCount).toList()
        val memberSubscribedWorkbooksReverseOrderByCurrentDay =
            IntStream
                .range(1, 1 + totalMemberSubscribedWorkbookCount)
                .mapToObj {
                    MemberSubscribedWorkbook(
                        it.toLong(),
                        true,
                        (1 + totalMemberSubscribedWorkbookCount) - it,
                    )
                }.toList()

        val notSubscribeWorkbookIds =
            workbooksOrderBySubscriptionCount
                .filter {
                    !activeWorkbookIds.contains(
                        it.id.toInt(),
                    )
                }.map { it.id.toInt() }

        // when
        val delegator =
            AuthMainViewWorkbookOrderDelegator(
                memberSubscribedWorkbooksReverseOrderByCurrentDay,
            )

        // then
        val orderedWorkbooks = delegator.order(OrderTargetWorkBooks(WorkBooks(workbooksOrderBySubscriptionCount)))
        assertEquals(totalWorkbookCount, orderedWorkbooks.workbooks.workbookData.size)

        val expectedOrderedWorkbookIds = activeWorkbookIds + notSubscribeWorkbookIds
        for (i in expectedOrderedWorkbookIds.indices) {
            assertEquals(expectedOrderedWorkbookIds[i].toLong(), orderedWorkbooks.workbooks.workbookData[i].id)
        }
    }

    @Test
    fun `워크북과 멤버 구독 완료 워크북만 주어지는 경우`() {
        // given
        val totalWorkbookCount = 10
        val workbooksOrderBySubscriptionCount =
            IntStream
                .range(1, 1 + totalWorkbookCount)
                .mapToObj {
                    WorkBook(
                        it.toLong(),
                        URL("http://localhost:8080/$it"),
                        "title$it",
                        "description$it",
                        "category$it",
                        LocalDateTime.now(),
                        emptyList(),
                        (1 + totalWorkbookCount) - it.toLong(),
                    )
                }.toList()

        /**
         * 1 : inactive, current day 5
         * 2 : inactive, current day 4
         * 3 : inactive, current day 3
         * 4 : inactive, current day 2
         * 5 : inactive, current day 1
         */
        val totalMemberSubscribedWorkbookCount = 5
        val inActiveWorkbookIds = IntStream.range(1, 1 + totalMemberSubscribedWorkbookCount).toList()
        val memberSubscribedWorkbooksReverseOrderByCurrentDay =
            IntStream
                .range(1, 1 + totalMemberSubscribedWorkbookCount)
                .mapToObj {
                    MemberSubscribedWorkbook(
                        it.toLong(),
                        false,
                        (1 + totalMemberSubscribedWorkbookCount) - it,
                    )
                }.toList()

        val notSubscribeWorkbookIds =
            workbooksOrderBySubscriptionCount
                .filter {
                    !inActiveWorkbookIds.contains(
                        it.id.toInt(),
                    )
                }.map { it.id.toInt() }

        // when
        val delegator =
            AuthMainViewWorkbookOrderDelegator(
                memberSubscribedWorkbooksReverseOrderByCurrentDay,
            )

        // then
        val orderedWorkbooks = delegator.order(OrderTargetWorkBooks(WorkBooks(workbooksOrderBySubscriptionCount)))
        assertEquals(totalWorkbookCount, orderedWorkbooks.workbooks.workbookData.size)

        val expectedOrderedWorkbookIds = notSubscribeWorkbookIds + inActiveWorkbookIds
        for (i in expectedOrderedWorkbookIds.indices) {
            assertEquals(expectedOrderedWorkbookIds[i].toLong(), orderedWorkbooks.workbooks.workbookData[i].id)
        }
    }
}