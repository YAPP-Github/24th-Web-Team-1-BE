package com.few.api.repo.dao.problem

import com.few.api.repo.dao.problem.query.SelectProblemAnswerQuery
import com.few.api.repo.dao.problem.query.SelectProblemQuery
import com.few.api.repo.dao.problem.record.SelectProblemAnswerRecord
import com.few.api.repo.dao.problem.record.SelectProblemRecord
import jooq.jooq_dsl.tables.Problem
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Component

@Component
class ProblemDao(
    private val dslContext: DSLContext
) {
    fun selectProblemContents(query: SelectProblemQuery): SelectProblemRecord {
        return dslContext.select(
            Problem.PROBLEM.ID.`as`(SelectProblemRecord::id.name),
            Problem.PROBLEM.TITLE.`as`(SelectProblemRecord::title.name),
            DSL.field("JSON_UNQUOTE({0})", String::class.java, Problem.PROBLEM.CONTENTS)
                .`as`(SelectProblemRecord::contents.name)
        )
            .from(Problem.PROBLEM)
            .where(Problem.PROBLEM.ID.eq(query.problemId))
            .fetchOneInto(SelectProblemRecord::class.java)
            ?: throw RuntimeException("Problem with ID ${query.problemId} not found") // TODO: 에러 표준화
    }

    fun selectProblemAnswer(query: SelectProblemAnswerQuery): SelectProblemAnswerRecord {
        return dslContext.select(
            Problem.PROBLEM.ID.`as`(SelectProblemAnswerRecord::id.name),
            Problem.PROBLEM.ANSWER.`as`(SelectProblemAnswerRecord::answer.name),
            Problem.PROBLEM.EXPLANATION.`as`(SelectProblemAnswerRecord::explanation.name)
        )
            .from(Problem.PROBLEM)
            .where(Problem.PROBLEM.ID.eq(query.problemId))
            .fetchOneInto(SelectProblemAnswerRecord::class.java)
            ?: throw RuntimeException("Problem Answer with ID ${query.problemId} not found") // TODO: 에러 표준화
    }
}