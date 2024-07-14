package com.few.api.repo.dao.problem

import com.few.api.repo.dao.problem.command.InsertProblemsCommand
import com.few.api.repo.dao.problem.query.SelectProblemAnswerQuery
import com.few.api.repo.dao.problem.query.SelectProblemQuery
import com.few.api.repo.dao.problem.query.SelectProblemsByArticleIdQuery
import com.few.api.repo.dao.problem.record.ProblemIdsRecord
import com.few.api.repo.dao.problem.record.SelectProblemAnswerRecord
import com.few.api.repo.dao.problem.record.SelectProblemRecord
import com.few.api.repo.dao.problem.support.ContentsJsonMapper
import jooq.jooq_dsl.tables.Problem
import org.jooq.DSLContext
import org.jooq.JSON
import org.jooq.impl.DSL
import org.springframework.stereotype.Component

@Component
class ProblemDao(
    private val dslContext: DSLContext,
    private val contentsJsonMapper: ContentsJsonMapper,
) {
    fun selectProblemContents(query: SelectProblemQuery): SelectProblemRecord? {
        return dslContext.select(
            Problem.PROBLEM.ID.`as`(SelectProblemRecord::id.name),
            Problem.PROBLEM.TITLE.`as`(SelectProblemRecord::title.name),
            DSL.field("JSON_UNQUOTE({0})", String::class.java, Problem.PROBLEM.CONTENTS)
                .`as`(SelectProblemRecord::contents.name)
        )
            .from(Problem.PROBLEM)
            .where(Problem.PROBLEM.ID.eq(query.problemId))
            .and(Problem.PROBLEM.DELETED_AT.isNull)
            .fetchOneInto(SelectProblemRecord::class.java)
    }

    fun selectProblemAnswer(query: SelectProblemAnswerQuery): SelectProblemAnswerRecord? {
        return dslContext.select(
            Problem.PROBLEM.ID.`as`(SelectProblemAnswerRecord::id.name),
            Problem.PROBLEM.ANSWER.`as`(SelectProblemAnswerRecord::answer.name),
            Problem.PROBLEM.EXPLANATION.`as`(SelectProblemAnswerRecord::explanation.name)
        )
            .from(Problem.PROBLEM)
            .where(Problem.PROBLEM.ID.eq(query.problemId))
            .and(Problem.PROBLEM.DELETED_AT.isNull)
            .fetchOneInto(SelectProblemAnswerRecord::class.java)
    }

    fun selectProblemsByArticleId(query: SelectProblemsByArticleIdQuery): ProblemIdsRecord? {
        val articleId = query.articleId

        return dslContext.select()
            .from(Problem.PROBLEM)
            .where(Problem.PROBLEM.ARTICLE_ID.eq(articleId))
            .and(Problem.PROBLEM.DELETED_AT.isNull)
            .fetch()
            .map { it[Problem.PROBLEM.ID] }
            .let { ProblemIdsRecord(it) }
    }

    fun insertProblems(command: List<InsertProblemsCommand>) {
        dslContext.batch(
            command.map {
                dslContext.insertInto(Problem.PROBLEM)
                    .set(Problem.PROBLEM.ARTICLE_ID, it.articleId)
                    .set(Problem.PROBLEM.CREATOR_ID, it.createrId)
                    .set(Problem.PROBLEM.TITLE, it.title)
                    .set(Problem.PROBLEM.CONTENTS, JSON.valueOf(contentsJsonMapper.toJson(it.contents)))
                    .set(Problem.PROBLEM.ANSWER, it.answer)
                    .set(Problem.PROBLEM.EXPLANATION, it.explanation)
            }
        ).execute()
    }
}