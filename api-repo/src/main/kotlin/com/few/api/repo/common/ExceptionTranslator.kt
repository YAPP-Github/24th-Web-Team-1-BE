package com.few.api.repo.common

import org.jooq.ExecuteContext
import org.jooq.ExecuteListener
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator
import org.springframework.jdbc.support.SQLExceptionTranslator

class ExceptionTranslator : ExecuteListener {

    override fun exception(context: ExecuteContext) {
        val dialect = context.configuration().dialect()
        val translator: SQLExceptionTranslator = SQLErrorCodeSQLExceptionTranslator(dialect.name)
        context.exception(
            translator
                .translate("Access database using Jooq", context.sql(), context.sqlException()!!)
        )
    }
}