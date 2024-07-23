package com.few.api.repo.common

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jooq.ExecuteContext
import org.jooq.ExecuteListener
import org.jooq.tools.StopWatch
import org.springframework.context.ApplicationEventPublisher

class PerformanceListener(
    private val applicationEventPublisher: ApplicationEventPublisher,
) : ExecuteListener {
    private val log = KotlinLogging.logger {}

    private var watch: StopWatch? = null
    override fun executeStart(ctx: ExecuteContext) {
        super.executeStart(ctx)
        watch = StopWatch()
    }

    override fun executeEnd(ctx: ExecuteContext) {
        super.executeEnd(ctx)
        if (watch!!.split() > 5000000000L) { // 5 seconds
            log.warn { "Slow Query Detected: \n${ctx.query()}" }
            applicationEventPublisher.publishEvent(SlowQueryEvent(ctx.query().toString()))
        }
    }
}