package com.few.api.repo.dao.member

import com.few.api.repo.dao.member.command.InsertMemberCommand
import com.few.api.repo.dao.member.query.SelectMemberByEmailQuery
import com.few.api.repo.dao.member.query.SelectWriterQuery
import com.few.api.repo.dao.member.query.SelectWritersQuery
import com.few.api.repo.dao.member.record.MemberIdRecord
import com.few.api.repo.dao.member.record.WriterRecord
import com.few.data.common.code.MemberType
import jooq.jooq_dsl.tables.Member
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository

@Repository
class MemberDao(
    private val dslContext: DSLContext,
) {

    fun selectWriter(query: SelectWriterQuery): WriterRecord? {
        return selectWriterQuery(query)
            .fetchOneInto(WriterRecord::class.java)
    }

    fun selectWriterQuery(query: SelectWriterQuery) = dslContext.select(
        Member.MEMBER.ID.`as`(WriterRecord::writerId.name),
        DSL.jsonGetAttributeAsText(Member.MEMBER.DESCRIPTION, "name").`as`(WriterRecord::name.name),
        DSL.jsonGetAttribute(Member.MEMBER.DESCRIPTION, "url").`as`(WriterRecord::url.name)
    )
        .from(Member.MEMBER)
        .where(Member.MEMBER.ID.eq(query.writerId))
        .and(Member.MEMBER.TYPE_CD.eq(MemberType.WRITER.code))
        .and(Member.MEMBER.DELETED_AT.isNull)

    fun selectWriters(query: SelectWritersQuery): List<WriterRecord> {
        return selectWritersQuery(query)
            .fetchInto(WriterRecord::class.java)
    }

    fun selectWritersQuery(query: SelectWritersQuery) =
        dslContext.select(
            Member.MEMBER.ID.`as`(WriterRecord::writerId.name),
            DSL.jsonGetAttributeAsText(Member.MEMBER.DESCRIPTION, "name")
                .`as`(WriterRecord::name.name),
            DSL.jsonGetAttribute(Member.MEMBER.DESCRIPTION, "url").`as`(WriterRecord::url.name)
        )
            .from(Member.MEMBER)
            .where(Member.MEMBER.ID.`in`(query.writerIds))
            .and(Member.MEMBER.TYPE_CD.eq(MemberType.WRITER.code))
            .and(Member.MEMBER.DELETED_AT.isNull)
            .orderBy(Member.MEMBER.ID.asc())

    fun selectMemberByEmail(query: SelectMemberByEmailQuery): MemberIdRecord? {
        return selectMemberByEmailQuery(query)
            .fetchOneInto(MemberIdRecord::class.java)
    }

    fun selectMemberByEmailQuery(query: SelectMemberByEmailQuery) = dslContext.select(
        Member.MEMBER.ID.`as`(MemberIdRecord::memberId.name)
    )
        .from(Member.MEMBER)
        .where(Member.MEMBER.EMAIL.eq(query.email))
        .and(Member.MEMBER.DELETED_AT.isNull)

    fun insertMember(command: InsertMemberCommand): Long? {
        val result = insertMemberCommand(command)
            .returning(Member.MEMBER.ID)
            .fetchOne()

        return result?.getValue(Member.MEMBER.ID)
    }

    fun insertMemberCommand(command: InsertMemberCommand) =
        dslContext.insertInto(Member.MEMBER)
            .set(Member.MEMBER.EMAIL, command.email)
            .set(Member.MEMBER.TYPE_CD, command.memberType.code)
}