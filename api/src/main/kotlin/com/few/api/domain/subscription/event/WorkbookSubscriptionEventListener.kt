package com.few.api.domain.subscription.event

import com.few.api.client.subscription.SubscriptionClient
import com.few.api.client.subscription.dto.WorkbookSubscriptionArgs
import com.few.api.domain.subscription.event.dto.WorkbookSubscriptionEvent
import com.few.api.domain.subscription.service.WorkbookService
import com.few.api.domain.subscription.service.dto.ReadWorkbookTitleInDto
import com.few.api.exception.common.NotFoundException
import com.few.api.repo.dao.subscription.SubscriptionDao
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class WorkbookSubscriptionEventListener(
    private val subscriptionDao: SubscriptionDao,
    private val subscriptionClient: SubscriptionClient,
    private val workbookService: WorkbookService
) {

    @Async
    @EventListener
    fun handleWorkbookSubscriptionEvent(event: WorkbookSubscriptionEvent) {
        val title = ReadWorkbookTitleInDto(event.workbookId).let { dto ->
            workbookService.readWorkbookTitle(dto)?.workbookTitle
                ?: throw NotFoundException("workbook.notfound.id")
        }
        subscriptionDao.countAllSubscriptionStatus().let { record ->
            WorkbookSubscriptionArgs(
                totalSubscriptions = record.totalSubscriptions,
                activeSubscriptions = record.activeSubscriptions,
                workbookTitle = title
            ).let { args ->
                subscriptionClient.announceWorkbookSubscription(args)
            }
        }
    }
}