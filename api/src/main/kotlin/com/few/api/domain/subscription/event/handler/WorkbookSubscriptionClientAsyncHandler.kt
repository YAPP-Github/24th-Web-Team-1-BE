package com.few.api.domain.subscription.event.handler

import com.few.api.config.ApiThreadPoolConfig.Companion.DISCORD_HOOK_EVENT_POOL
import com.few.api.domain.common.exception.NotFoundException
import com.few.api.domain.subscription.client.ApiSubscriptionClient
import com.few.api.domain.subscription.client.dto.WorkbookSubscriptionArgs
import com.few.api.domain.subscription.repo.SubscriptionDao
import com.few.api.domain.subscription.service.SubscriptionWorkbookService
import com.few.api.domain.subscription.service.dto.ReadWorkbookTitleInDto
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class WorkbookSubscriptionClientAsyncHandler(
    private val subscriptionDao: SubscriptionDao,
    private val apiSubscriptionClient: ApiSubscriptionClient,
    private val workbookService: SubscriptionWorkbookService,
) {
    @Async(value = DISCORD_HOOK_EVENT_POOL)
    fun sendSubscriptionEvent(workbookId: Long) {
        val title =
            workbookService.readWorkbookTitle(ReadWorkbookTitleInDto(workbookId))?.workbookTitle
                ?: throw NotFoundException("workbook.notfound.id")

        subscriptionDao.countAllSubscriptionStatus().also { record ->
            apiSubscriptionClient.announceWorkbookSubscription(
                WorkbookSubscriptionArgs(
                    totalSubscriptions = record.totalSubscriptions,
                    activeSubscriptions = record.activeSubscriptions,
                    workbookTitle = title,
                ),
            )
        }
    }
}