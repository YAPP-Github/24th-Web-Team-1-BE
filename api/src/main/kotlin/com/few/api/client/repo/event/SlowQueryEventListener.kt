package com.few.api.client.repo.event

import com.few.api.client.repo.RepoClient
import com.few.api.client.repo.dto.RepoAlterArgs
import com.few.api.config.ApiThreadPoolConfig.Companion.DISCORD_HOOK_EVENT_POOL
import com.few.api.repo.common.SlowQueryEvent
import org.slf4j.MDC
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class SlowQueryEventListener(
    private val repoClient: RepoClient,
) {

    @Async(value = DISCORD_HOOK_EVENT_POOL)
    @EventListener
    fun handleSlowQueryEvent(event: SlowQueryEvent) {
        RepoAlterArgs(
            requestURL = MDC.get("Request-URL") ?: "",
            query = event.slowQuery
        ).let {
            repoClient.announceRepoAlter(it)
        }
    }
}