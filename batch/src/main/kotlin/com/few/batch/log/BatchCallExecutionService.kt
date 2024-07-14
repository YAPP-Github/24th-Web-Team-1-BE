package com.few.batch.log

import jooq.jooq_dsl.Tables.BATCH_CALL_EXECUTION
import org.jooq.DSLContext
import org.jooq.JSON
import org.springframework.stereotype.Service

@Service
class BatchCallExecutionService(
    private val dslContext: DSLContext,
) {
    fun execute(status: Boolean, jsonDescription: String) {
        dslContext.insertInto(BATCH_CALL_EXECUTION)
            .set(BATCH_CALL_EXECUTION.STATUS, status)
            .set(BATCH_CALL_EXECUTION.DESCRIPTION, JSON.valueOf(jsonDescription))
            .execute()
    }
}