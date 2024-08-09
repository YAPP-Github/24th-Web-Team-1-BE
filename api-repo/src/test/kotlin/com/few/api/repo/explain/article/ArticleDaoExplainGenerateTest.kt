package com.few.api.repo.explain.article

import com.few.api.repo.dao.article.ArticleDao
import com.few.api.repo.dao.article.command.InsertFullArticleRecordCommand
import com.few.api.repo.dao.article.query.SelectArticleIdByWorkbookIdAndDayQuery
import com.few.api.repo.dao.article.query.SelectArticleRecordQuery
import com.few.api.repo.dao.article.query.SelectWorkBookArticleRecordQuery
import com.few.api.repo.dao.article.query.SelectWorkbookMappedArticleRecordsQuery
import com.few.api.repo.explain.InsertUpdateExplainGenerator
import com.few.api.repo.explain.ResultGenerator
import com.few.api.repo.jooq.JooqTestSpec
import com.few.data.common.code.CategoryType
import io.github.oshai.kotlinlogging.KotlinLogging
import jooq.jooq_dsl.tables.ArticleIfo
import jooq.jooq_dsl.tables.ArticleMst
import jooq.jooq_dsl.tables.MappingWorkbookArticle
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.net.URL

@Tag("explain")
class ArticleDaoExplainGenerateTest : JooqTestSpec() {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var dslContext: DSLContext

    @Autowired
    private lateinit var articleDao: ArticleDao

    @BeforeEach
    fun setUp() {
        log.debug { "===== start setUp =====" }
        dslContext.deleteFrom(ArticleMst.ARTICLE_MST).execute()
        dslContext.deleteFrom(ArticleIfo.ARTICLE_IFO).execute()
        dslContext.deleteFrom(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE).execute()
        dslContext.insertInto(ArticleMst.ARTICLE_MST)
            .set(ArticleMst.ARTICLE_MST.ID, 1L)
            .set(ArticleMst.ARTICLE_MST.MEMBER_ID, 1L)
            .set(ArticleMst.ARTICLE_MST.MAIN_IMAGE_URL, "http://localhost:8080/image1.jpg")
            .set(ArticleMst.ARTICLE_MST.TITLE, "this is title1")
            .set(ArticleMst.ARTICLE_MST.CATEGORY_CD, CategoryType.fromCode(0)!!.code)
            .execute()
        dslContext.insertInto(ArticleIfo.ARTICLE_IFO)
            .set(ArticleIfo.ARTICLE_IFO.ARTICLE_MST_ID, 1L)
            .set(ArticleIfo.ARTICLE_IFO.CONTENT, "this is content1")
            .execute()
        dslContext.insertInto(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE)
            .set(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID, 1L)
            .set(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.ARTICLE_ID, 1L)
            .set(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.DAY_COL, 0)
            .execute()
        log.debug { "===== finish setUp =====" }
    }

    @Test
    fun selectArticleRecordQueryExplain() {
        val query = SelectArticleRecordQuery(1L).let {
            articleDao.selectArticleRecordQuery(it)
        }

        val explain = dslContext.explain(query).toString()

        ResultGenerator.execute(query, explain, "selectArticleRecordQueryExplain")
    }

    @Test
    fun selectWorkBookArticleRecordQueryExplain() {
        val query = SelectWorkBookArticleRecordQuery(1L, 1L).let {
            articleDao.selectWorkBookArticleRecordQuery(it)
        }

        val explain = dslContext.explain(query).toString()

        ResultGenerator.execute(query, explain, "selectWorkBookArticleRecordQueryExplain")
    }

    @Test
    fun selectWorkbookMappedArticleRecordsQueryExplain() {
        val query = SelectWorkbookMappedArticleRecordsQuery(1L).let {
            articleDao.selectWorkbookMappedArticleRecordsQuery(it)
        }

        val explain = dslContext.explain(query).toString()

        ResultGenerator.execute(query, explain, "selectWorkbookMappedArticleRecordsQueryExplain")
    }

    @Test
    fun insertArticleMstCommandExplain() {
        val command = InsertFullArticleRecordCommand(
            100L,
            URL("http://localhost:8080/image1.jpg"),
            "this is title1",
            CategoryType.fromCode(0)!!.code,
            "this is content1"
        ).let {
            articleDao.insertArticleMstCommand(it)
        }

        val explain = InsertUpdateExplainGenerator.execute(dslContext, command.sql, command.bindValues)

        ResultGenerator.execute(command, explain, "insertArticleMstCommandExplain")
    }

    @Test
    fun insertArticleIfoCommandExplain() {
        val command = InsertFullArticleRecordCommand(
            100L,
            URL("http://localhost:8080/image1.jpg"),
            "this is title1",
            CategoryType.fromCode(0)!!.code,
            "this is content1"
        ).let {
            articleDao.insertArticleIfoCommand(1L, it)
        }

        val explain = InsertUpdateExplainGenerator.execute(dslContext, command.sql, command.bindValues)

        ResultGenerator.execute(command, explain, "insertArticleIfoCommandExplain")
    }

    @Test
    fun selectArticleIdByWorkbookIdAndDayQueryExplain() {
        val query = SelectArticleIdByWorkbookIdAndDayQuery(1L, 1).let {
            articleDao.selectArticleIdByWorkbookIdAndDayQuery(it)
        }

        val explain = dslContext.explain(query).toString()

        ResultGenerator.execute(query, explain, "selectArticleIdByWorkbookIdAndDayQueryExplain")
    }

    @Test
    fun selectArticleContentsQueryExplain() {
        val query = setOf(1L).let {
            articleDao.selectArticleContentsQuery(it)
        }

        val explain = dslContext.explain(query).toString()

        ResultGenerator.execute(query, explain, "selectArticleContentsQueryExplain")
    }
}