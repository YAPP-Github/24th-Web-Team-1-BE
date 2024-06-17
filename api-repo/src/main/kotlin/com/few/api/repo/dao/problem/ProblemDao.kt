package com.few.api.repo.dao.problem

import com.few.api.repo.dao.problem.query.SelectProblemQuery
import com.few.api.repo.dao.problem.record.SelectProblemRecord
import jooq.jooq_dsl.tables.Problem
import org.jooq.DSLContext
import org.springframework.stereotype.Component

@Component
class ProblemDao(
    private val dslContext: DSLContext
) {
    fun selectProblem(query: SelectProblemQuery): SelectProblemRecord {
        val result = dslContext.select(Problem.PROBLEM.ID, Problem.PROBLEM.TITLE, Problem.PROBLEM.CONTENTS)
            .from(Problem.PROBLEM)
            .where(Problem.PROBLEM.ID.eq(query.problemId))
            .fetchOne()

        if (result == null) {
            throw RuntimeException("Problem with ID ${query.problemId} not found") // TODO: 에러 표준화
        }

        return SelectProblemRecord(
            result.get(Problem.PROBLEM.ID),
            result.get(Problem.PROBLEM.TITLE),
            result.get(Problem.PROBLEM.CONTENTS).data()
        )
    }
}