package com.few.api.repo.explain

import org.jooq.DSLContext
import org.jooq.Query

class ExplainGenerator {
    companion object {
        /**
         * Execute EXPLAIN and EXPLAIN ANALYZE FORMAT=TREE
         */
        fun execute(dslContext: DSLContext, query: Query): String {
            val sql = query.sql
            val values = query.bindValues
            mapSqlAndValues(sql, values).let {
                val explain = StringBuilder()
                explain.append("EXPLAIN $it\n")
                explain.append("\n")
                dslContext.fetch("EXPLAIN $it").let {
                    explain.append(it)
                }
                explain.append("\n\n")
                explain.append("EXPLAIN ANALYZE FORMAT=TREE $it\n")
                explain.append("\n")
                dslContext.fetch("EXPLAIN ANALYZE FORMAT=TREE $it").let {
                    it.forEach { record ->
                        explain.append(record[0].toString())
                    }
                }
                return explain.toString()
            }
        }

        /**
         * Execute EXPLAIN
         */
        fun explain(dslContext: DSLContext, query: Query): String {
            val sql = query.sql
            val values = query.bindValues
            mapSqlAndValues(sql, values).let {
                val explain = StringBuilder()
                explain.append("EXPLAIN $it\n")
                explain.append("\n")
                dslContext.fetch("EXPLAIN $it").let {
                    explain.append(it)
                }
                return explain.toString()
            }
        }

        /**
         * Execute EXPLAIN ANALYZE FORMAT=TREE
         */
        fun analyzeFormatTree(dslContext: DSLContext, query: Query): String {
            val sql = query.sql
            val values = query.bindValues
            mapSqlAndValues(sql, values).let {
                val explain = StringBuilder()
                explain.append("EXPLAIN ANALYZE FORMAT=TREE $it\n")
                explain.append("\n")
                dslContext.fetch("EXPLAIN ANALYZE FORMAT=TREE $it").let {
                    it.forEach { record ->
                        explain.append(record[0].toString())
                    }
                }
                return explain.toString()
            }
        }

        private fun mapSqlAndValues(sql: String, values: List<Any>) =
            values.foldIndexed(sql) { index, acc, value ->
                if (value is String) {
                    acc.replaceFirst("?", "'$value'")
                } else {
                    acc.replaceFirst("?", "$value")
                }
            }
    }
}