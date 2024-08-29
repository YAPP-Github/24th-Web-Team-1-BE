package com.few.api.domain.subscription.handler

import com.few.api.client.subscription.SubscriptionClient
import com.few.api.client.subscription.dto.WorkbookSubscriptionArgs
import com.few.api.config.ApiThreadPoolConfig.Companion.DISCORD_HOOK_EVENT_POOL
import com.few.api.domain.subscription.service.SubscriptionWorkbookService
import com.few.api.domain.subscription.service.dto.ReadWorkbookTitleInDto
import com.few.api.exception.common.NotFoundException
import com.few.api.repo.dao.subscription.SubscriptionDao
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class WorkbookSubscriptionClientAsyncHandler(
    private val subscriptionDao: SubscriptionDao,
    private val subscriptionClient: SubscriptionClient,
    private val workbookService: SubscriptionWorkbookService,
) {

    @Async(value = DISCORD_HOOK_EVENT_POOL)
    fun sendSubscriptionEvent(workbookId: Long) {
        val title =
            workbookService.readWorkbookTitle(ReadWorkbookTitleInDto(workbookId))?.workbookTitle
                ?: throw NotFoundException("workbook.notfound.id")

        subscriptionDao.countAllSubscriptionStatus().also { record ->
            subscriptionClient.announceWorkbookSubscription(
                WorkbookSubscriptionArgs(
                    totalSubscriptions = record.totalSubscriptions,
                    activeSubscriptions = record.activeSubscriptions,
                    workbookTitle = title
                )
            )
        }
    }
}