package com.few.api.domain.subscription.event

import com.few.api.domain.subscription.event.dto.WorkbookSubscriptionEvent
import com.few.api.domain.subscription.handler.SendWorkbookArticleAsyncHandler
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class WorkbookSubscriptionAfterCompletionEventListener(
    private val sendWorkbookArticleAsyncHandler: SendWorkbookArticleAsyncHandler,
) {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleEvent(event: WorkbookSubscriptionEvent) {
        sendWorkbookArticleAsyncHandler.sendWorkbookArticle(
            event.memberId,
            event.workbookId,
            event.articleDayCol.toByte()
        )
    }
}