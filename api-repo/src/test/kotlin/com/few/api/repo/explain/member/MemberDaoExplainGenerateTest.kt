package com.few.api.repo.explain.member

import com.few.api.repo.dao.member.MemberDao
import com.few.api.repo.dao.member.command.DeleteMemberCommand
import com.few.api.repo.dao.member.command.InsertMemberCommand
import com.few.api.repo.dao.member.command.UpdateDeletedMemberTypeCommand
import com.few.api.repo.dao.member.command.UpdateMemberTypeCommand
import com.few.api.repo.dao.member.query.BrowseWorkbookWritersQuery
import com.few.api.repo.dao.member.query.SelectMemberByEmailNotConsiderDeletedAtQuery
import com.few.api.repo.dao.member.query.SelectMemberByEmailQuery
import com.few.api.repo.dao.member.query.SelectWriterQuery
import com.few.api.repo.dao.member.support.WriterDescriptionJsonMapper
import com.few.api.repo.explain.ExplainGenerator
import com.few.api.repo.explain.InsertUpdateExplainGenerator
import com.few.api.repo.explain.ResultGenerator
import com.few.api.repo.jooq.JooqTestSpec
import com.few.data.common.code.MemberType
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jooq.DSLContext
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@Tag("explain")
class MemberDaoExplainGenerateTest : JooqTestSpec() {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var dslContext: DSLContext

    @Autowired
    private lateinit var memberDao: MemberDao

    @Autowired
    private lateinit var writerDescriptionJsonMapper: WriterDescriptionJsonMapper

    @Test
    fun selectWriterQueryExplain() {
        val query = SelectWriterQuery(1).let {
            memberDao.selectWriterQuery(it)
        }

        val explain = ExplainGenerator.execute(dslContext, query)

        ResultGenerator.execute(query, explain, "selectWriterQueryExplain")
    }

    @Test
    fun selectWritersQueryExplain() {
        val query = listOf(2L, 3L).let {
            memberDao.selectWritersQuery(it)
        }

        val explain = ExplainGenerator.execute(dslContext, query)

        ResultGenerator.execute(query, explain, "selectWritersQueryExplain")
    }

    @Test
    fun selectWritersQueryExplainByWorkbookIds() {
        val query = BrowseWorkbookWritersQuery(listOf(2L, 3L)).let {
            memberDao.selectWritersQuery(it)
        }

        val explain = ExplainGenerator.execute(dslContext, query)

        ResultGenerator.execute(query, explain, "selectWritersQueryExplainByWorkbookIds")
    }

    @Test
    fun selectMemberByEmailQueryExplain() {
        val query = SelectMemberByEmailQuery("member@gmail.com").let {
            memberDao.selectMemberByEmailQuery(it)
        }

        val explain = ExplainGenerator.execute(dslContext, query)

        ResultGenerator.execute(query, explain, "selectMemberByEmailQueryExplainExplain")
    }

    @Test
    fun selectMemberByEmailNotConsiderDeletedAtQueryExplain() {
        val query = SelectMemberByEmailNotConsiderDeletedAtQuery("test@gmail.com").let {
            memberDao.selectMemberByEmailQuery(it)
        }

        val explain = ExplainGenerator.execute(dslContext, query)

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
    fun selectMemberIdAndTypeQueryExplain() {
        val query = 1L.let {
            memberDao.selectMemberIdAndTypeQuery(it)
        }

        val explain = ExplainGenerator.execute(dslContext, query)

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