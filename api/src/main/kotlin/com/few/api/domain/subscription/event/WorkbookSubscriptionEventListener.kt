package com.few.api.domain.subscription.event

import com.few.api.domain.subscription.event.dto.WorkbookSubscriptionEvent
import com.few.api.domain.subscription.event.handler.WorkbookSubscriptionClientAsyncHandler
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class WorkbookSubscriptionEventListener(
    private val workbookSubscriptionClientAsyncHandler: WorkbookSubscriptionClientAsyncHandler,
) {
    @EventListener
    fun handleEvent(event: WorkbookSubscriptionEvent) {
        workbookSubscriptionClientAsyncHandler.sendSubscriptionEvent(event.workbookId)
    }
}