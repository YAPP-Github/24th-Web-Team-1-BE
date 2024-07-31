package com.few.api.repo.jooq

import jooq.jooq_dsl.tables.Member
import org.jooq.DSLContext
import org.jooq.JSON
import org.jooq.exception.DataAccessException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

class _SampleJooqTest : JooqTestSpec() {

    private val log: org.slf4j.Logger = LoggerFactory.getLogger(_SampleJooqTest::class.java)

    @Autowired
    private lateinit var dslContext: DSLContext

    companion object {
        const val EMAIL = "test1@gmail.com"
        const val TYPECD: Byte = 1
    }

    @BeforeEach
    fun setUp() {
        log.debug("===== start setUp =====")
        dslContext.deleteFrom(Member.MEMBER).execute()
        dslContext.insertInto(Member.MEMBER)
            .set(Member.MEMBER.EMAIL, EMAIL)
            .set(Member.MEMBER.TYPE_CD, TYPECD)
            .execute()
        dslContext.commit().execute()
        log.debug("===== finish setUp =====")
    }

    @Test
    @Transactional
    fun `새로운 정보를 저장합니다`() {
        // given
        val email = "test2@gmail.com"
        val typeCd: Byte = 1

        // when
        val result = dslContext.insertInto(Member.MEMBER)
            .set(Member.MEMBER.EMAIL, email)
            .set(Member.MEMBER.TYPE_CD, typeCd)
            .execute()

        // then
        assert(result > 0)
    }

    @Test
    @Transactional
    fun `이메일이 중복되는 경우 저장에 실패합니다`() {
        // when & then
        assertThrows<DuplicateKeyException> {
            dslContext.insertInto(Member.MEMBER)
                .set(Member.MEMBER.EMAIL, EMAIL)
                .set(Member.MEMBER.TYPE_CD, TYPECD)
                .execute()
        }
    }

    @Test
    @Transactional
    fun `이메일 값을 입력하지 않은면 저장에 실패합니다`() {
        // when & then
        assertThrows<DataAccessException> {
            dslContext.insertInto(Member.MEMBER)
                .set(Member.MEMBER.TYPE_CD, TYPECD)
                .execute()
        }
    }

    @Test
    @Transactional
    fun `타입 코드 값을 입력하지 않은면 저장에 실패합니다`() {
        // when & then
        assertThrows<DataAccessException> {
            dslContext.insertInto(Member.MEMBER)
                .set(Member.MEMBER.EMAIL, EMAIL)
                .execute()
        }
    }

    @Test
    fun `이메일 일치 조건을 통해 정보를 조회합니다`() {
        // when
        val result = dslContext.selectFrom(Member.MEMBER)
            .where(Member.MEMBER.EMAIL.eq(EMAIL))
            .and(Member.MEMBER.DELETED_AT.isNull())
            .fetchOne()

        // then
        assert(result != null)
        assert(result!!.email == EMAIL)
        assert(result.typeCd == TYPECD)
        assert(result.description.equals(JSON.json("{}")))
        assert(result.createdAt != null)
        assert(result.deletedAt == null)
    }

    @Test
    fun `이메일 불일치 조건을 통해 유저를 조회합니다`() {
        // when
        val result = dslContext.selectFrom(Member.MEMBER)
            .where(Member.MEMBER.EMAIL.ne("test2@gmail.com"))
            .and(Member.MEMBER.DELETED_AT.isNull())
            .fetch()

        // then
        assert(result.isNotEmpty())
    }

    @Test
    @Transactional
    fun `이메일을 수정합니다`() {
        // given
        val newEmail = "test2@gmail.com"

        // when
        val update = dslContext.update(Member.MEMBER)
            .set(Member.MEMBER.EMAIL, newEmail)
            .where(Member.MEMBER.EMAIL.eq(EMAIL))
            .and(Member.MEMBER.DELETED_AT.isNull())
            .execute()

        val result = dslContext.selectFrom(Member.MEMBER)
            .where(Member.MEMBER.EMAIL.eq(newEmail))
            .and(Member.MEMBER.DELETED_AT.isNull())
            .fetchOne()

        // then
        assert(update > 0)
        assert(result != null)
        assert(result!!.email == newEmail)
    }

    @Test
    @Transactional
    fun `타입 코드를 수정합니다`() {
        // given
        val newTypeCd: Byte = 2

        // when
        val update = dslContext.update(Member.MEMBER)
            .set(Member.MEMBER.TYPE_CD, newTypeCd)
            .where(Member.MEMBER.EMAIL.eq(EMAIL))
            .and(Member.MEMBER.DELETED_AT.isNull())
            .execute()

        val result = dslContext.selectFrom(Member.MEMBER)
            .where(Member.MEMBER.EMAIL.eq(EMAIL))
            .and(Member.MEMBER.DELETED_AT.isNull())
            .fetchOne()

        // then
        assert(update > 0)
        assert(result != null)
        assert(result!!.typeCd == newTypeCd)
    }

    @Test
    @Rollback(false)
    @Transactional
    fun `소프트 삭제를 수행합니다`() {
        // given
        val deleteTarget = dslContext.selectFrom(Member.MEMBER)
            .where(Member.MEMBER.EMAIL.eq(EMAIL))
            .and(Member.MEMBER.DELETED_AT.isNull())
            .fetchOne()

        // when
        val softDelete = dslContext.update(Member.MEMBER)
            .set(Member.MEMBER.DELETED_AT, LocalDateTime.now())
            .where(Member.MEMBER.EMAIL.eq(EMAIL))
            .and(Member.MEMBER.DELETED_AT.isNull())
            .execute()

        val result = dslContext.selectFrom(Member.MEMBER)
            .where(Member.MEMBER.EMAIL.eq(EMAIL))
            .fetchOne()

        // then
        assert(deleteTarget != null)
        assert(softDelete > 0)
        assert(result != null)
    }
}