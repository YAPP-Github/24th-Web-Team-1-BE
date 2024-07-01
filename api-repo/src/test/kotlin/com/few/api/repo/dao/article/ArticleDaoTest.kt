package com.few.api.repo.dao.article

import com.few.api.repo.dao.article.query.SelectArticleRecordQuery
import com.few.api.repo.dao.article.query.SelectWorkBookArticleRecordQuery
import com.few.api.repo.dao.article.query.SelectWorkbookMappedArticleRecordsQuery
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
import java.net.URL

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
            .set(ArticleMst.ARTICLE_MST.MAIN_IMAGE_URL, "http://localhost:8080/image1.jpg")
            .set(ArticleMst.ARTICLE_MST.TITLE, "this is title1")
            .set(ArticleMst.ARTICLE_MST.CATEGORY_CD, 0)
            .execute()
        dslContext.insertInto(ArticleIfo.ARTICLE_IFO)
            .set(ArticleIfo.ARTICLE_IFO.ARTICLE_MST_ID, 1L)
            .set(ArticleIfo.ARTICLE_IFO.CONTENT, "this is content1")
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
        assertNotNull(result!!)
        assertEquals(1L, result.articleId)
        assertEquals(1L, result.writerId)
        assertEquals(URL("http://localhost:8080/image1.jpg"), result.mainImageURL)
        assertEquals("this is title1", result.title)
        assertEquals("this is content1", result.content)
        assertEquals(0, result.category)
    }

    @Test
    @Transactional
    fun `학습지 Id와 아티클 Id를 통해 학습지에서의 아티클 Day가 포함된 아티클 정보를 조회합니다`() {
        // given
        setMappingWorkbookArticleData(1)
        val query = SelectWorkBookArticleRecordQuery(1L, 1L)

        // when
        val result = query.let {
            articleDao.selectWorkBookArticleRecord(it)
        }

        // then
        assertNotNull(result!!)
        assertEquals(1L, result.articleId)
        assertEquals(1L, result.writerId)
        assertEquals(URL("http://localhost:8080/image1.jpg"), result.mainImageURL)
        assertEquals("this is title1", result.title)
        assertEquals("this is content1", result.content)
        assertEquals(0, result.category)
        assertEquals(1L, result.day)
    }

    @Test
    @Transactional
    fun `학습지에 속한 아티클 정보를 조회합니다`() {
        // given
        val totalCount = 5
        setMappingWorkbookArticleData(totalCount)
        val query = SelectWorkbookMappedArticleRecordsQuery(1L)

        // when
        val result = query.let {
            articleDao.selectWorkbookMappedArticleRecords(it)
        }

        // then
        assertNotNull(result)
        assertEquals(totalCount, result.size)
        for (i in result.indices) {
            assertEquals(i + 1L, result[i].articleId)
            assertEquals(1L, result[i].writerId)
            assertEquals(URL("http://localhost:8080/image${i + 1}.jpg"), result[i].mainImageURL)
            assertEquals("this is title${i + 1}", result[i].title)
            assertEquals("this is content${i + 1}", result[i].content)
            assertEquals(0, result[i].category) // todo fix
        }
    }

    private fun setMappingWorkbookArticleData(count: Int) {
        log.debug("===== start setMappingWorkbookArticleData =====")
        dslContext.deleteFrom(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE).execute()
        for (i in 1..count) {
            setMappingWorkbookArticle(1L, i.toLong(), i)
        }
        for (i in 2..count) {
            setArticleMST(i.toLong())
            setArticleInfo(i.toLong())
        }
        log.debug("===== finish setMappingWorkbookArticleData =====")
    }

    private fun setMappingWorkbookArticle(workbookId: Long, articleId: Long, day: Int) {
        dslContext.insertInto(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE)
            .set(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID, workbookId)
            .set(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.ARTICLE_ID, articleId)
            .set(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.DAY_COL, day)
            .execute()
    }

    private fun setArticleMST(id: Long) {
        dslContext.insertInto(ArticleMst.ARTICLE_MST)
            .set(ArticleMst.ARTICLE_MST.ID, id)
            .set(ArticleMst.ARTICLE_MST.MEMBER_ID, 1L)
            .set(ArticleMst.ARTICLE_MST.MAIN_IMAGE_URL, "http://localhost:8080/image$id.jpg")
            .set(ArticleMst.ARTICLE_MST.TITLE, "this is title$id")
            .set(ArticleMst.ARTICLE_MST.CATEGORY_CD, 0)
            .execute()
    }

    private fun setArticleInfo(id: Long) {
        dslContext.insertInto(ArticleIfo.ARTICLE_IFO)
            .set(ArticleIfo.ARTICLE_IFO.ARTICLE_MST_ID, id)
            .set(ArticleIfo.ARTICLE_IFO.CONTENT, "this is content$id")
            .execute()
    }
}