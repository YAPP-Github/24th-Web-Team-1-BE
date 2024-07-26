package com.few.api.repo.common

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jooq.ExecuteContext
import org.jooq.ExecuteListener
import org.jooq.impl.DSL

class NativeSQLLogger : ExecuteListener {
    private val log = KotlinLogging.logger {}

    override fun executeEnd(ctx: ExecuteContext) {
        ctx.query()?.let {
            log.debug { "Query executed: ${DSL.using(ctx.configuration()).renderInlined(ctx.query())}" }
        }
    }
}