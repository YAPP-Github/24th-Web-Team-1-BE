package com.few.api.repo.explain.article

import com.few.api.repo.dao.article.ArticleViewHisDao
import com.few.api.repo.dao.article.command.ArticleViewHisCommand
import com.few.api.repo.dao.article.query.ArticleViewHisCountQuery
import com.few.api.repo.explain.ResultGenerator
import com.few.api.repo.jooq.JooqTestSpec
import io.github.oshai.kotlinlogging.KotlinLogging
import jooq.jooq_dsl.tables.*
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@Tag("explain")
class ArticleViewHisDaoExplainGenerateTest : JooqTestSpec() {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var dslContext: DSLContext

    @Autowired
    private lateinit var articleViewHisDao: ArticleViewHisDao

    @BeforeEach
    fun setUp() {
        log.debug { "===== start setUp =====" }
        dslContext.deleteFrom(ArticleViewHis.ARTICLE_VIEW_HIS).execute()
        dslContext.insertInto(ArticleViewHis.ARTICLE_VIEW_HIS)
            .set(ArticleViewHis.ARTICLE_VIEW_HIS.ARTICLE_MST_ID, 1L)
            .set(ArticleViewHis.ARTICLE_VIEW_HIS.MEMBER_ID, 1L)
            .execute()
        log.debug { "===== finish setUp =====" }
    }

    @Test
    fun countArticleViewsQueryExplain() {
        val query = ArticleViewHisCountQuery(1L).let {
            articleViewHisDao.countArticleViewsQuery(it)
        }

        val explain = dslContext.explain(query).toString()
        ResultGenerator.execute(query, explain, "selectArticleViewCountQueryExplain")
    }

    @Test
    fun insertArticleViewHisCommandExplain() {
        val command = ArticleViewHisCommand(
            articleId = 1L,
            memberId = 1L
        ).let {
            articleViewHisDao.insertArticleViewHisCommand(it)
        }

        var sql = command.sql
        val bindValues = command.bindValues
        sql = bindValues.foldIndexed(sql) { index, acc, value ->
            acc.replaceFirst("?", "\"$value\"")
        }

        val explain = dslContext.explain(DSL.query(sql)).toString()

        ResultGenerator.execute(command, explain, "insertArticleViewHisCommandExplain")
    }
}