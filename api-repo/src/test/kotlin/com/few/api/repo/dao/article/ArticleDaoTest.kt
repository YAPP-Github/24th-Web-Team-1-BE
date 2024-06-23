package com.few.api.repo.dao.article

import com.few.api.repo.dao.article.query.SelectArticleRecordQuery
import com.few.api.repo.dao.article.query.SelectWorkBookArticleRecordQuery
import com.few.api.repo.jooq.JooqTestSpec
import jooq.jooq_dsl.tables.ArticleIfo
import jooq.jooq_dsl.tables.ArticleMst
import jooq.jooq_dsl.tables.MappingWorkbookArticle
import org.jooq.DSLContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class ArticleDaoTest : JooqTestSpec() {

    private val log: org.slf4j.Logger = LoggerFactory.getLogger(ArticleDaoTest::class.java)

    @Autowired
    private lateinit var dslContext: DSLContext

    @Autowired
    private lateinit var articleDao: ArticleDao

    @BeforeEach
    fun setUp() {
        log.debug("===== start setUp =====")
        dslContext.deleteFrom(ArticleMst.ARTICLE_MST).execute()
        dslContext.deleteFrom(ArticleIfo.ARTICLE_IFO).execute()
        dslContext.insertInto(ArticleMst.ARTICLE_MST)
            .set(ArticleMst.ARTICLE_MST.ID, 1L)
            .set(ArticleMst.ARTICLE_MST.MEMBER_ID, 1L)
            .set(ArticleMst.ARTICLE_MST.MAIN_IMAGE_URL, "http://localhost:8080/image.jpg")
            .set(ArticleMst.ARTICLE_MST.TITLE, "this is title")
            .set(ArticleMst.ARTICLE_MST.CATEGORY_CD, 0)
            .execute()
        dslContext.insertInto(ArticleIfo.ARTICLE_IFO)
            .set(ArticleIfo.ARTICLE_IFO.ARTICLE_MST_ID, 1L)
            .set(ArticleIfo.ARTICLE_IFO.CONTENT, "this is content")
            .execute()
        log.debug("===== finish setUp =====")
    }

    @Test
    @Transactional
    fun `아티클 Id를 통해 아티클 정보를 조회합니다`() {
        // given
        val query = SelectArticleRecordQuery(1L)

        // when
        val result = query.let {
            articleDao.selectArticleRecord(it)
        }

        // then
        assertNotNull(result)
        assertEquals(1L, result.articleId)
        assertEquals(1L, result.writerId)
        assertEquals("http://localhost:8080/image.jpg", result.mainImageURL)
        assertEquals("this is title", result.title)
        assertEquals("this is content", result.content)
        assertEquals(0, result.category)
    }

    @Test
    @Transactional
    fun `학습지 Id와 아티클 Id를 통해 학습지에서의 아티클 Day가 포함된 아티클 정보를 조회합니다`() {
        // given
        setMappingWorkbookArticleData()
        val query = SelectWorkBookArticleRecordQuery(1L, 1L)

        // when
        val result = query.let {
            articleDao.selectWorkBookArticleRecord(it)
        }

        // then
        assertNotNull(result)
        assertEquals(1L, result.articleId)
        assertEquals(1L, result.writerId)
        assertEquals("http://localhost:8080/image.jpg", result.mainImageURL)
        assertEquals("this is title", result.title)
        assertEquals("this is content", result.content)
        assertEquals(0, result.category)
        assertEquals(1L, result.day)
    }

    private fun setMappingWorkbookArticleData() {
        log.debug("===== start setMappingWorkbookArticleData =====")
        dslContext.deleteFrom(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE).execute()
        setMappingWorkbookArticle(1L, 1L, 1)
        setMappingWorkbookArticle(1L, 2L, 2)
        setMappingWorkbookArticle(1L, 3L, 3)
        log.debug("===== finish setMappingWorkbookArticleData =====")
    }

    private fun setMappingWorkbookArticle(workbookId: Long, articleId: Long, day: Int) {
        dslContext.insertInto(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE)
            .set(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID, workbookId)
            .set(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.ARTICLE_ID, articleId)
            .set(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.DAY_COL, day)
            .execute()
    }
}