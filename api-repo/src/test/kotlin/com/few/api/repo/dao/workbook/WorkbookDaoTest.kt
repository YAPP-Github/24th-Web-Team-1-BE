package com.few.api.repo.dao.workbook

import com.few.api.repo.dao.workbook.query.SelectWorkBookRecordQuery
import com.few.api.repo.jooq.JooqTestSpec
import jooq.jooq_dsl.tables.Workbook
import org.jooq.DSLContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.net.URL

class WorkbookDaoTest : JooqTestSpec() {

    @Autowired
    private lateinit var dslContext: DSLContext

    @Autowired
    private lateinit var workbookDao: WorkbookDao

    @BeforeEach
    fun setUp() {
        dslContext.deleteFrom(Workbook.WORKBOOK).execute()
        dslContext.insertInto(Workbook.WORKBOOK)
            .set(Workbook.WORKBOOK.ID, 1)
            .set(Workbook.WORKBOOK.TITLE, "title1")
            .set(Workbook.WORKBOOK.MAIN_IMAGE_URL, "http://localhost:8080/image1.jpg")
            .set(Workbook.WORKBOOK.CATEGORY_CD, 0) // todo fix
            .set(Workbook.WORKBOOK.DESCRIPTION, "description1")
            .execute()
    }

    @Test
    fun `워크북 ID로 워크북을 조회합니다`() {
        // given
        val query = SelectWorkBookRecordQuery(1L)

        // when
        val result = query.let {
            workbookDao.selectWorkBook(it)
        }

        // then
        assertNotNull(result!!)
        assertEquals(1L, result.id)
        assertEquals("title1", result.title)
        assertEquals(URL("http://localhost:8080/image1.jpg"), result.mainImageUrl)
        assertEquals(0, result.category)
        assertEquals("description1", result.description)
    }
}