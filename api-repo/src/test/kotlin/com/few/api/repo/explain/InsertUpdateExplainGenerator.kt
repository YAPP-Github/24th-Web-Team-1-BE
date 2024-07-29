package com.few.api.repo.explain

import org.jooq.DSLContext
import org.jooq.JSON
import org.jooq.impl.DSL

class InsertUpdateExplainGenerator {
    companion object {
        fun execute(dslContext: DSLContext, sql: String, values: List<Any>): String {
            return dslContext.explain(
                DSL.query(
                    values.foldIndexed(sql) { index, acc, value ->
                        if (value is JSON) {
                            value.toString().replace("\"", "\\\"").let {
                                return@foldIndexed acc.replaceFirst("?", "\"$it\"")
                            }
                        } else {
                            acc.replaceFirst("?", "\"$value\"")
                        }
                    }
                )
            ).toString()
        }
    }
}