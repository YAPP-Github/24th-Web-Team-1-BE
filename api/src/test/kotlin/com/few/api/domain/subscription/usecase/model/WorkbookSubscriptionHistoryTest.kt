package com.few.api.domain.subscription.usecase.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class WorkbookSubscriptionHistoryTest {
    @Test
    fun `새로 생성된 구독인데 구독 상태가 존재하는 경우`() {
        // given & when & then
        assertThrows(IllegalArgumentException::class.java) {
            WorkbookSubscriptionHistory(
                isNew = true,
                workbookSubscriptionStatus =
                    WorkbookSubscriptionStatus(
                        workbookId = 1,
                        isActiveSub = true,
                        day = 1,
                    ),
            )
        }
    }

    @Test
    fun `새로 생성된 구독이 아닌데 구독 상태가 존재하지 않는 경우`() {
        // given & when & then
        assertThrows(IllegalArgumentException::class.java) {
            WorkbookSubscriptionHistory(
                isNew = false,
            )
        }
    }

    @Test
    fun `새로 생성된 구독이 아니고 구독 상태가 취소된 경우`() {
        // given
        val workbookSubscriptionHistory =
            WorkbookSubscriptionHistory(
                isNew = false,
                workbookSubscriptionStatus =
                    WorkbookSubscriptionStatus(
                        workbookId = 1,
                        isActiveSub = false,
                        day = 1,
                    ),
            )

        // when
        val isCancel = workbookSubscriptionHistory.isCancelSub

        // then
        assertTrue(isCancel)
    }

    @Test
    fun `새로 생성된 구독이 아니고 구독 상태가 취소되지 않은 경우`() {
        // given
        val workbookSubscriptionHistory =
            WorkbookSubscriptionHistory(
                isNew = false,
                workbookSubscriptionStatus =
                    WorkbookSubscriptionStatus(
                        workbookId = 1,
                        isActiveSub = true,
                        day = 1,
                    ),
            )

        // when
        val isCancel = workbookSubscriptionHistory.isCancelSub

        // then
        assertFalse(isCancel)
    }

    @Test
    fun `새로 생성된 구독인 경우 구독 날짜`() {
        // given
        val workbookSubscriptionHistory =
            WorkbookSubscriptionHistory(
                isNew = true,
            )

        // when
        val subDay = workbookSubscriptionHistory.subDay

        // then
        assertEquals(1, subDay)
    }

    @Test
    fun `새로 생성된 구독이 아닌 경우 구독 날짜`() {
        // given
        val workbookSubscriptionHistory =
            WorkbookSubscriptionHistory(
                isNew = false,
                workbookSubscriptionStatus =
                    WorkbookSubscriptionStatus(
                        workbookId = 1,
                        isActiveSub = true,
                        day = 2,
                    ),
            )

        // when
        val subDay = workbookSubscriptionHistory.subDay

        // then
        assertEquals(2, subDay)
    }

    @Nested
    inner class CancelledWorkbookSubscriptionHistoryTest {
        @Test
        fun `구독 취소된 히스토리가 취소되지 않은 구독 히스토리로 생성되는 경우`() {
            // given
            val workbookSubscriptionHistory =
                WorkbookSubscriptionHistory(
                    isNew = false,
                    workbookSubscriptionStatus =
                        WorkbookSubscriptionStatus(
                            workbookId = 1,
                            isActiveSub = true,
                            day = 1,
                        ),
                )

            // when & then
            assertThrows(IllegalArgumentException::class.java) {
                CancelledWorkbookSubscriptionHistory(workbookSubscriptionHistory)
            }
        }

        @Test
        fun `구독 취소된 히스토리가 새로운 구독 히스토리로 생성되는 경우`() {
            // given
            val workbookSubscriptionHistory =
                WorkbookSubscriptionHistory(
                    isNew = true,
                )

            // when & then
            assertThrows(IllegalArgumentException::class.java) {
                CancelledWorkbookSubscriptionHistory(workbookSubscriptionHistory)
            }
        }

        @Test
        fun `구독 취소된 히스토리가 종료된 경우`() {
            // given
            val workbookSubscriptionHistory =
                WorkbookSubscriptionHistory(
                    isNew = false,
                    workbookSubscriptionStatus =
                        WorkbookSubscriptionStatus(
                            workbookId = 1,
                            isActiveSub = false,
                            day = 1,
                        ),
                )

            // when
            val cancelledWorkbookSubscriptionHistory = CancelledWorkbookSubscriptionHistory(workbookSubscriptionHistory)
            val isSubEnd = cancelledWorkbookSubscriptionHistory.isSubEnd(1)

            // then
            assertTrue(isSubEnd)
        }

        @Test
        fun `구독 취소된 히스토리가 종료되지 않은 경우`() {
            // given
            val workbookSubscriptionHistory =
                WorkbookSubscriptionHistory(
                    isNew = false,
                    workbookSubscriptionStatus =
                        WorkbookSubscriptionStatus(
                            workbookId = 1,
                            isActiveSub = false,
                            day = 1,
                        ),
                )

            // when
            val cancelledWorkbookSubscriptionHistory = CancelledWorkbookSubscriptionHistory(workbookSubscriptionHistory)
            val isSubEnd = cancelledWorkbookSubscriptionHistory.isSubEnd(2)

            // then
            assertFalse(isSubEnd)
        }
    }
}