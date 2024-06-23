package com.few.api.repo.dao.member

import com.few.api.repo.dao.member.query.SelectWriterQuery
import com.few.api.repo.dao.member.query.SelectWritersQuery
import com.few.api.repo.dao.member.support.WriterDescription
import com.few.api.repo.dao.member.support.WriterDescriptionJsonMapper
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
import java.util.stream.LongStream
import kotlin.streams.toList

class MemberDaoTest : JooqTestSpec() {

    private val log: org.slf4j.Logger = LoggerFactory.getLogger(MemberDaoTest::class.java)

    @Autowired
    private lateinit var dslContext: DSLContext

    @Autowired
    private lateinit var memberDao: MemberDao

    @Autowired
    private lateinit var writerDescriptionJsonMapper: WriterDescriptionJsonMapper

    @BeforeEach
    fun setUp() {
        log.debug("===== start setUp =====")
        dslContext.deleteFrom(Member.MEMBER).execute()
        dslContext.insertInto(Member.MEMBER)
            .set(Member.MEMBER.ID, 1)
            .set(Member.MEMBER.EMAIL, "member@gmail.com")
            .set(Member.MEMBER.TYPE_CD, 0) // todo fix
            .execute()

        val writerDescription = writerDescriptionJsonMapper.toJson(
            WriterDescription("few2", URL("http://localhost:8080/writers/url2"))
        )

        dslContext.insertInto(Member.MEMBER)
            .set(Member.MEMBER.ID, 2)
            .set(Member.MEMBER.EMAIL, "writer2@gmail.com")
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
        assertEquals("few2", result.name)
        assertEquals(URL("http://localhost:8080/writers/url2"), result.url)
    }

    @Test
    @Transactional
    fun `Id 목록으로 작가를 조회합니다`() {
        // given
        val addCount = 5
        setMoreWriters(addCount)
        val query = LongStream.range(2L, 2L + (addCount + 1)).toList().let {
            SelectWritersQuery(it)
        }

        // when
        val results = query.let {
            memberDao.selectWriters(it)
        }

        // then
        assertNotNull(results)
        assertEquals(addCount + 1, results.size)
        for (i in 0 until addCount) {
            assertEquals(2L + i, results[i].writerId)
            assertEquals("few${i + 2}", results[i].name)
            assertEquals(URL("http://localhost:8080/writers/url${i + 2}"), results[i].url)
        }
    }

    fun setMoreWriters(count: Int) {
        for (i in 3 until 3 + count) {
            val writerDescription = writerDescriptionJsonMapper.toJson(
                WriterDescription("few$i", URL("http://localhost:8080/writers/url$i"))
            )
            dslContext.insertInto(Member.MEMBER)
                .set(Member.MEMBER.ID, i.toLong())
                .set(Member.MEMBER.EMAIL, "writer$i@gmail.com")
                .set(Member.MEMBER.TYPE_CD, 1) // todo fix
                .set(Member.MEMBER.DESCRIPTION, JSON.valueOf(writerDescription))
                .execute()
        }
    }
}