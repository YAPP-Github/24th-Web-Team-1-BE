package com.few.api.repo.dao.problem

import com.few.api.repo.dao.problem.query.SelectProblemsByArticleIdQuery
import com.few.api.repo.dao.problem.support.Content
import com.few.api.repo.dao.problem.support.Contents
import com.few.api.repo.dao.problem.support.ContentsJsonMapper
import com.few.api.repo.jooq.JooqTestSpec
import jooq.jooq_dsl.tables.Problem
import org.jooq.DSLContext
import org.jooq.JSON
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

class ProblemDaoTest : JooqTestSpec() {
    private val log: org.slf4j.Logger = LoggerFactory.getLogger(ProblemDaoTest::class.java)

    @Autowired
    private lateinit var dslContext: DSLContext

    @Autowired
    private lateinit var problemDao: ProblemDao

    @Autowired
    private lateinit var contentsJsonMapper: ContentsJsonMapper

    @BeforeEach
    fun setUp() {
        log.debug("===== start setUp =====")
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
        log.debug("===== finish setUp =====")
    }

    @Test
    fun `아티클 Id로 문제를 조회합니다`() {
        // given
        val query = SelectProblemsByArticleIdQuery(1L)

        // when
        val result = problemDao.selectProblemsByArticleId(query)

        // then
        assertNotNull(result!!)
        assertEquals(1, result.problemIds.size)
        assertEquals(1, result.problemIds[0])
    }
}