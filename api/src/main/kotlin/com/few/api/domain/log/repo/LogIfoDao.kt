package com.few.api.domain.log.repo

import com.few.api.domain.log.repo.command.InsertLogCommand
import jooq.jooq_dsl.tables.LogIfo
import org.jooq.DSLContext
import org.jooq.JSON
import org.springframework.stereotype.Repository

@Repository
class LogIfoDao(
    private val dslContext: DSLContext,
) {
    fun insertLogIfo(command: InsertLogCommand): Long? =
        dslContext
            .insertInto(LogIfo.LOG_IFO)
            .set(LogIfo.LOG_IFO.HISTORY, JSON.valueOf(command.history))
            .returning(LogIfo.LOG_IFO.ID)
            .fetchOne()
            ?.id
}