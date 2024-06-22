package com.few.api.repo.dao.problem

import com.few.api.repo.dao.problem.query.SelectProblemsByArticleIdQuery
import com.few.api.repo.dao.problem.record.ProblemIdsRecord
import jooq.jooq_dsl.tables.Problem
import org.jooq.DSLContext
import org.springframework.stereotype.Component

@Component
class ProblemDao(
    private val dslContext: DSLContext
) {
    fun selectProblemsByArticleId(query: SelectProblemsByArticleIdQuery): ProblemIdsRecord {
        val articleId = query.articleId

        return dslContext.select()
            .from(Problem.PROBLEM)
            .where(Problem.PROBLEM.ARTICLE_ID.eq(articleId))
            .fetch()
            .map { it[Problem.PROBLEM.ID] }
            .let { ProblemIdsRecord(it) }
    }
}