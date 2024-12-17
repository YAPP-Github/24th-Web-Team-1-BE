package com.few.api.domain.subscription.usecase.model

class CancelledWorkbookSubscriptionHistory(
    workbookSubscriptionHistory: WorkbookSubscriptionHistory,
) : WorkbookSubscriptionHistory(
        workbookSubscriptionHistory,
    ) {
    init {
        require(isCancelSub) {
            "CanceledWorkbookSubscriptionHistory is not for active subscription."
        }
    }

    /**
     * 구독이 종료되었는지 여부 확인
     */
    fun isSubEnd(lastDay: Int): Boolean = (lastDay <= workbookSubscriptionStatus!!.day)
}