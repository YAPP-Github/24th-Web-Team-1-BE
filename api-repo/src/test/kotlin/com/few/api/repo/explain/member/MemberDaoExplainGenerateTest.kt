package com.few.api.repo.explain.member

import com.few.api.repo.dao.member.MemberDao
import com.few.api.repo.dao.member.command.DeleteMemberCommand
import com.few.api.repo.dao.member.command.InsertMemberCommand
import com.few.api.repo.dao.member.command.UpdateDeletedMemberTypeCommand
import com.few.api.repo.dao.member.command.UpdateMemberTypeCommand
import com.few.api.repo.dao.member.query.*
import com.few.api.repo.dao.member.support.WriterDescription
import com.few.api.repo.dao.member.support.WriterDescriptionJsonMapper
import com.few.api.repo.explain.InsertUpdateExplainGenerator
import com.few.api.repo.explain.ResultGenerator
import com.few.api.repo.jooq.JooqTestSpec
import com.few.data.common.code.MemberType
import io.github.oshai.kotlinlogging.KotlinLogging
import jooq.jooq_dsl.tables.Member
import org.jooq.DSLContext
import org.jooq.JSON
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.net.URL

@Tag("explain")
class MemberDaoExplainGenerateTest : JooqTestSpec() {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var dslContext: DSLContext

    @Autowired
    private lateinit var memberDao: MemberDao

    @Autowired
    private lateinit var writerDescriptionJsonMapper: WriterDescriptionJsonMapper

    @BeforeEach
    fun setUp() {
        log.debug { "===== start setUp =====" }
        dslContext.deleteFrom(Member.MEMBER).execute()
        dslContext.insertInto(Member.MEMBER)
            .set(Member.MEMBER.ID, 1)
            .set(Member.MEMBER.EMAIL, "member@gmail.com")
            .set(Member.MEMBER.TYPE_CD, MemberType.NORMAL.code)
            .execute()

        val writerDescription = writerDescriptionJsonMapper.toJson(
            WriterDescription(
                "few2",
                URL("http://localhost:8080/writers/url2"),
                URL("https://github.com/user-attachments/assets/28df9078-488c-49d6-9375-54ce5a250742")
            )
        )

        dslContext.insertInto(Member.MEMBER)
            .set(Member.MEMBER.ID, 2)
            .set(Member.MEMBER.EMAIL, "writer2@gmail.com")
            .set(Member.MEMBER.TYPE_CD, MemberType.WRITER.code)
            .set(Member.MEMBER.DESCRIPTION, JSON.valueOf(writerDescription))
            .execute()

        dslContext.insertInto(Member.MEMBER)
            .set(Member.MEMBER.ID, 3)
            .set(Member.MEMBER.EMAIL, "writer3@gmail.com")
            .set(Member.MEMBER.TYPE_CD, MemberType.WRITER.code)
            .set(Member.MEMBER.DESCRIPTION, JSON.valueOf(writerDescription))
            .execute()
        log.debug { "===== finish setUp =====" }
    }

    @Test
    fun selectWriterQueryExplain() {
        val query = SelectWriterQuery(1).let {
            memberDao.selectWriterQuery(it)
        }

        val explain = dslContext.explain(query).toString()

        ResultGenerator.execute(query, explain, "selectWriterQueryExplain")
    }

    @Test
    fun selectWritersQueryExplain() {
        val query = listOf(2L, 3L).let {
            memberDao.selectWritersQuery(it)
        }

        val explain = dslContext.explain(query).toString()

        ResultGenerator.execute(query, explain, "selectWritersQueryExplain")
    }

    @Test
    fun selectWritersQueryExplainByWorkbookIds() {
        val query = BrowseWorkbookWritersQuery(listOf(2L, 3L)).let {
            memberDao.selectWritersQuery(it)
        }

        val explain = dslContext.explain(query).toString()

        ResultGenerator.execute(query, explain, "selectWritersQueryExplainByWorkbookIds")
    }

    @Test
    fun selectMemberByEmailQueryExplain() {
        val query = SelectMemberByEmailQuery("member@gmail.com").let {
            memberDao.selectMemberByEmailQuery(it)
        }

        val explain = dslContext.explain(query).toString()

        ResultGenerator.execute(query, explain, "selectMemberByEmailQueryExplainExplain")
    }

    @Test
    fun selectMemberByEmailNotConsiderDeletedAtQueryExplain() {
        val query = SelectMemberByEmailNotConsiderDeletedAtQuery("test@gmail.com").let {
            memberDao.selectMemberByEmailQuery(it)
        }

        val explain = dslContext.explain(query).toString()

        ResultGenerator.execute(query, explain, "selectMemberByEmailNotConsiderDeletedAtQueryExplain")
    }

    @Test
    fun insertMemberCommandExplain() {
        val command = InsertMemberCommand(
            email = "test100@gmail.com",
            memberType = MemberType.NORMAL
        ).let {
            memberDao.insertMemberCommand(it)
        }

        val explain = InsertUpdateExplainGenerator.execute(dslContext, command.sql, command.bindValues)

        ResultGenerator.execute(command, explain, "insertMemberCommandExplain")
    }

    @Test
    fun selectMemberEmailQueryExplain() {
        val query = SelectMemberEmailQuery(1).let {
            memberDao.selectMemberEmailQuery(it)
        }

        val explain = ExplainGenerator.execute(dslContext, query)

        ResultGenerator.execute(query, explain, "selectMemberEmailQueryExplain")
    }

    @Test
    fun selectMemberIdAndTypeQueryExplain() {
        val query = 1L.let {
            memberDao.selectMemberIdAndTypeQuery(it)
        }

        val explain = dslContext.explain(query).toString()

        ResultGenerator.execute(query, explain, "selectMemberIdAndTypeQueryExplain")
    }

    @Test
    fun updateMemberTypeCommandExplain() {
        val command = UpdateMemberTypeCommand(
            id = 1,
            memberType = MemberType.WRITER
        ).let {
            memberDao.updateMemberTypeCommand(it)
        }

        val explain = InsertUpdateExplainGenerator.execute(dslContext, command.sql, command.bindValues)

        ResultGenerator.execute(command, explain, "updateMemberTypeCommandExplain")
    }

    @Test
    fun updateDeletedMemberTypeCommandExplain() {
        val command = UpdateDeletedMemberTypeCommand(
            id = 1,
            memberType = MemberType.WRITER
        ).let {
            memberDao.updateMemberTypeCommand(it)
        }

        val explain = InsertUpdateExplainGenerator.execute(dslContext, command.sql, command.bindValues)

        ResultGenerator.execute(command, explain, "updateDeletedMemberTypeCommandExplain")
    }

    @Test
    fun deleteMemberCommandExplain() {
        val command = DeleteMemberCommand(
            memberId = 1
        ).let {
            memberDao.deleteMemberCommand(it)
        }

        val explain = InsertUpdateExplainGenerator.execute(dslContext, command.sql, command.bindValues)

        ResultGenerator.execute(command, explain, "deleteMemberCommandExplain")
    }
}