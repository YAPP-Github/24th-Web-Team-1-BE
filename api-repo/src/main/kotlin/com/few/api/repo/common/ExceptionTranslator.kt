package com.few.api.repo.common

import org.jooq.ExecuteContext
import org.jooq.ExecuteListener
import org.springframework.jdbc.support.SQLExceptionTranslator

class ExceptionTranslator(
    private val translator: SQLExceptionTranslator,
) : ExecuteListener {

    override fun exception(context: ExecuteContext) {
        context.exception(
            translator
                .translate("Access database using Jooq", context.sql(), context.sqlException()!!)
        )
    }
}