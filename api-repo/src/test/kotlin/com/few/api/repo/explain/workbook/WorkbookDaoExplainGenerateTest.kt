package com.few.api.repo.explain.workbook

import com.few.api.repo.dao.workbook.WorkbookDao
import com.few.api.repo.dao.workbook.command.InsertWorkBookCommand
import com.few.api.repo.dao.workbook.command.MapWorkBookToArticleCommand
import com.few.api.repo.dao.workbook.query.BrowseWorkBookQueryWithSubscriptionCount
import com.few.api.repo.dao.workbook.query.SelectWorkBookRecordQuery
import com.few.api.repo.explain.InsertUpdateExplainGenerator
import com.few.api.repo.explain.ResultGenerator
import com.few.api.repo.jooq.JooqTestSpec
import com.few.data.common.code.CategoryType
import io.github.oshai.kotlinlogging.KotlinLogging
import jooq.jooq_dsl.tables.*
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.net.URL

@Tag("explain")
class WorkbookDaoExplainGenerateTest : JooqTestSpec() {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var dslContext: DSLContext

    @Autowired
    private lateinit var workbookDao: WorkbookDao

    @BeforeEach
    fun setUp() {
        log.debug { "===== start setUp =====" }
        dslContext.deleteFrom(Workbook.WORKBOOK).execute()
        dslContext.insertInto(Workbook.WORKBOOK)
            .set(Workbook.WORKBOOK.ID, 1)
            .set(Workbook.WORKBOOK.TITLE, "title1")
            .set(Workbook.WORKBOOK.MAIN_IMAGE_URL, "http://localhost:8080/image1.jpg")
            .set(Workbook.WORKBOOK.CATEGORY_CD, CategoryType.fromCode(0)!!.code)
            .set(Workbook.WORKBOOK.DESCRIPTION, "description1")
            .execute()
        log.debug { "===== finish setUp =====" }
    }

    @Test
    fun selectWorkBookQueryExplain() {
        val query = SelectWorkBookRecordQuery(1L).let {
            workbookDao.selectWorkBookQuery(it)
        }

        val explain = dslContext.explain(query).toString()

        ResultGenerator.execute(query, explain, "selectWorkBookQueryExplain")
    }

    @Test
    fun insertWorkBookCommandExplain() {
        val command = InsertWorkBookCommand(
            title = "title2",
            mainImageUrl = URL("http://localhost:8080/image2.jpg"),
            description = "description2",
            category = CategoryType.fromCode(0)!!.name
        ).let {
            workbookDao.insertWorkBookCommand(it)
        }

        val explain = InsertUpdateExplainGenerator.execute(dslContext, command.sql, command.bindValues)
        ResultGenerator.execute(command, explain, "insertWorkBookCommandExplain")
    }

    @Test
    fun browseWorkBookQueryNoConditionQueryExplain() {
        val query = BrowseWorkBookQueryWithSubscriptionCount(-1).let {
            workbookDao.browseWorkBookQuery(it)
        }

        val explain = dslContext.explain(query).toString()

        ResultGenerator.execute(query, explain, "browseWorkBookQueryNoConditionQuery")
    }

    @Test
    fun browseWorkBookQueryCategoryConditionExplain() {
        val query = BrowseWorkBookQueryWithSubscriptionCount(CategoryType.fromCode(0)!!.code).let {
            workbookDao.browseWorkBookQuery(it)
        }

        val explain = dslContext.explain(query).toString()

        ResultGenerator.execute(query, explain, "browseWorkBookQueryCategoryCondition")
    }

    @Test
    fun mapWorkBookToArticleCommandExplain() {
        val command = MapWorkBookToArticleCommand(
            workbookId = 1L,
            articleId = 1L,
            dayCol = 1
        ).let {
            workbookDao.mapWorkBookToArticleCommand(it)
        }

        val explain = InsertUpdateExplainGenerator.execute(dslContext, command.sql, command.bindValues)

        ResultGenerator.execute(command, explain, "mapWorkBookToArticleCommandExplain")
    }
}