package com.few.api.repo.explain.problem

import com.few.api.repo.dao.problem.ProblemDao
import com.few.api.repo.dao.problem.query.SelectProblemAnswerQuery
import com.few.api.repo.dao.problem.query.SelectProblemQuery
import com.few.api.repo.dao.problem.query.SelectProblemsByArticleIdQuery
import com.few.api.repo.dao.problem.support.Content
import com.few.api.repo.dao.problem.support.Contents
import com.few.api.repo.dao.problem.support.ContentsJsonMapper
import com.few.api.repo.explain.ResultGenerator
import com.few.api.repo.jooq.JooqTestSpec
import io.github.oshai.kotlinlogging.KotlinLogging
import jooq.jooq_dsl.tables.*
import org.jooq.DSLContext
import org.jooq.JSON
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@Tag("explain")
class ProblemDaoExplainGenerateTest : JooqTestSpec() {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var dslContext: DSLContext

    @Autowired
    private lateinit var problemDao: ProblemDao

    @Autowired
    private lateinit var contentsJsonMapper: ContentsJsonMapper

    @BeforeEach
    fun setUp() {
        log.debug { "===== start setUp =====" }
        dslContext.deleteFrom(Problem.PROBLEM).execute()
        val contents = Contents(
            listOf(
                Content(1, "content1"),
                Content(2, "content2")
            )
        ).let {
            contentsJsonMapper.toJson(it)
        }
        dslContext.insertInto(Problem.PROBLEM)
            .set(Problem.PROBLEM.ID, 1)
            .set(Problem.PROBLEM.ARTICLE_ID, 1)
            .set(Problem.PROBLEM.TITLE, "problem title")
            .set(Problem.PROBLEM.CONTENTS, JSON.valueOf(contents))
            .set(Problem.PROBLEM.ANSWER, "1")
            .set(Problem.PROBLEM.EXPLANATION, "explanation")
            .set(Problem.PROBLEM.CREATOR_ID, 1)
            .execute()
        log.debug { "===== finish setUp =====" }
    }

    @Test
    fun selectProblemContentsQueryExplain() {
        val query = SelectProblemQuery(1L).let {
            problemDao.selectProblemContentsQuery(it)
        }

        val explain = dslContext.explain(query).toString()

        ResultGenerator.execute(query, explain, "selectProblemContentsQuery")
    }

    @Test
    fun selectProblemAnswerQueryExplain() {
        val query = SelectProblemAnswerQuery(1L).let {
            problemDao.selectProblemAnswerQuery(it)
        }

        val explain = dslContext.explain(query).toString()

        ResultGenerator.execute(query, explain, "selectProblemAnswerQuery")
    }

    @Test
    fun selectProblemsByArticleIdQueryExplain() {
        val query = SelectProblemsByArticleIdQuery(1L).let {
            problemDao.selectProblemsByArticleIdQuery(it)
        }

        val explain = dslContext.explain(query).toString()

        ResultGenerator.execute(query, explain, "selectProblemsByArticleIdQuery")
    }
}