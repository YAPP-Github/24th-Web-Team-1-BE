package com.few.api.domain.common.repo.event

import com.few.api.domain.common.repo.client.ApiRepoClient
import com.few.api.domain.common.repo.client.dto.RepoAlterArgs
import com.few.api.config.ApiThreadPoolConfig.Companion.DISCORD_HOOK_EVENT_POOL
import repo.event.SlowQueryEvent
import org.slf4j.MDC
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class ApiSlowQueryEventListener(
    private val apiRepoClient: ApiRepoClient,
) {

    @Async(value = DISCORD_HOOK_EVENT_POOL)
    @EventListener
    fun handleSlowQueryEvent(event: SlowQueryEvent) {
        val args = RepoAlterArgs(
            requestURL = MDC.get("Request-URL") ?: "",
            query = event.slowQuery
        )
        apiRepoClient.announceRepoAlter(args)
    }
}