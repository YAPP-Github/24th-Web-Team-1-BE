package com.few.api.domain.subscription.usecase.model

class CancelledWorkbookSubscriptionHistory(
    workbookSubscriptionStatus: WorkbookSubscriptionStatus,
) : WorkbookSubscriptionHistory(
    false,
    workbookSubscriptionStatus
) {
    constructor(workbookSubscriptionHistory: WorkbookSubscriptionHistory) : this(
        workbookSubscriptionHistory.workbookSubscriptionStatus!!
    )

    init {
        require(isCancelSub) {
            "CanceledWorkbookSubscriptionHistory is not for active subscription."
        }
    }

    /**
     * 구독이 종료되었는지 여부 확인
     */
    fun isSubEnd(lastDay: Int): Boolean {
        return (lastDay <= workbookSubscriptionStatus!!.day)
    }
}