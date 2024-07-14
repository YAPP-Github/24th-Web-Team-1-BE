package com.few.api.repo.dao.log

import com.few.api.repo.dao.log.command.InsertLogCommand
import jooq.jooq_dsl.tables.LogIfo
import org.jooq.DSLContext
import org.jooq.JSON
import org.springframework.stereotype.Repository

@Repository
class LogIfoDao(
    private val dslContext: DSLContext,
) {

    fun insertLogIfo(command: InsertLogCommand): Long? {
        return dslContext.insertInto(LogIfo.LOG_IFO)
            .set(LogIfo.LOG_IFO.HISTORY, JSON.valueOf(command.history))
            .returning(LogIfo.LOG_IFO.ID)
            .fetchOne()?.id
    }
}