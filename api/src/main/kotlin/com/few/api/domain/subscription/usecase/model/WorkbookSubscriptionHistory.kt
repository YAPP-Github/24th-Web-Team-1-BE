package com.few.api.domain.subscription.usecase.model

open class WorkbookSubscriptionHistory(
    val isNew: Boolean,
    protected val workbookSubscriptionStatus: WorkbookSubscriptionStatus? = null,
) {

    constructor(workbookSubscriptionHistory: WorkbookSubscriptionHistory) : this(
        workbookSubscriptionHistory.isNew,
        workbookSubscriptionHistory.workbookSubscriptionStatus
    )

    init {
        if (isNew) {
            require(workbookSubscriptionStatus == null) {
                "If new subscription, workbookSubscriptionStatus should be null."
            }
        } else {
            require(workbookSubscriptionStatus != null) {
                "If not new subscription, workbookSubscriptionStatus should not be null."
            }
        }
    }

    /**
     * 이전 구독 히스토리가 존재하고, 현재 구독이 취소된 경우
     */
    val isCancelSub: Boolean
        get() {
            return !isNew && !workbookSubscriptionStatus!!.isActiveSub
        }

    val subDay: Int
        get() {
            return workbookSubscriptionStatus?.day ?: 1
        }
}