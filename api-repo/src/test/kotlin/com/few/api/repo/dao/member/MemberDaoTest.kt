package com.few.api.repo.dao.member

import com.few.api.repo.dao.member.query.SelectWriterQuery
import com.few.api.repo.dao.member.support.WriterDescription
import com.few.api.repo.dao.member.support.WriterDescriptionMapper
import com.few.api.repo.jooq.JooqTestSpec
import jooq.jooq_dsl.tables.Member
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.jooq.DSLContext
import org.jooq.JSON
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.net.URL

class MemberDaoTest : JooqTestSpec() {

    private val log: org.slf4j.Logger = LoggerFactory.getLogger(MemberDaoTest::class.java)

    @Autowired
    private lateinit var dslContext: DSLContext

    @Autowired
    private lateinit var memberDao: MemberDao

    @Autowired
    private lateinit var writerDescriptionMapper: WriterDescriptionMapper

    @BeforeEach
    fun setUp() {
        log.debug("===== start setUp =====")
        dslContext.deleteFrom(Member.MEMBER).execute()
        dslContext.insertInto(Member.MEMBER)
            .set(Member.MEMBER.ID, 1)
            .set(Member.MEMBER.EMAIL, "member@gmail.com")
            .set(Member.MEMBER.TYPE_CD, 0) // todo fix
            .execute()

        val writerDescription = writerDescriptionMapper.toJson(
            WriterDescription("few", URL("http://localhost:8080/writers/url"))
        )

        dslContext.insertInto(Member.MEMBER)
            .set(Member.MEMBER.ID, 2)
            .set(Member.MEMBER.EMAIL, "writer@gmail.com")
            .set(Member.MEMBER.TYPE_CD, 1) // todo fix
            .set(Member.MEMBER.DESCRIPTION, JSON.valueOf(writerDescription))
            .execute()
        log.debug("===== finish setUp =====")
    }

    @Test
    @Transactional
    fun `Id로 작가를 조회합니다`() {
        // given
        val query = SelectWriterQuery(2L)

        // when
        val result = query.let {
            memberDao.selectWriter(it)
        }

        assertNotNull(result)
        assertEquals(2L, result.writerId)
        assertEquals("few", result.name)
        assertEquals(URL("http://localhost:8080/writers/url"), result.url)
    }
}