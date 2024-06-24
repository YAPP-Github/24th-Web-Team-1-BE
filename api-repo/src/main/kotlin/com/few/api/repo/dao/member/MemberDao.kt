package com.few.api.repo.dao.member

import com.few.api.repo.dao.member.command.InsertMemberCommand
import com.few.api.repo.dao.member.query.SelectMemberByEmailQuery
import com.few.api.repo.dao.member.query.SelectWriterQuery
import com.few.api.repo.dao.member.record.MemberRecord
import com.few.api.repo.dao.member.record.WriterRecord
import jooq.jooq_dsl.tables.Member
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository

@Repository
class MemberDao(
    private val dslContext: DSLContext
) {

    fun selectWriter(query: SelectWriterQuery): WriterRecord {
        val writerId = query.writerId

        return dslContext.select(
            Member.MEMBER.ID.`as`(WriterRecord::writerId.name),
            DSL.jsonGetAttributeAsText(Member.MEMBER.DESCRIPTION, "name").`as`(WriterRecord::name.name),
            DSL.jsonGetAttribute(Member.MEMBER.DESCRIPTION, "url").`as`(WriterRecord::url.name)
        )
            .from(Member.MEMBER)
            .where(Member.MEMBER.ID.eq(writerId))
            .and(Member.MEMBER.TYPE_CD.eq(1)) // todo fix after considering the type_cd
            .and(Member.MEMBER.DELETED_AT.isNull)
            .fetchOneInto(WriterRecord::class.java)
            ?: throw IllegalArgumentException("cannot find writer record by writerId: $writerId")
    }

    fun selectMemberByEmail(query: SelectMemberByEmailQuery): MemberRecord {
        val email = query.email

        return dslContext.select(
            Member.MEMBER.ID.`as`(MemberRecord::memberId.name),
            DSL.jsonGetAttributeAsText(Member.MEMBER.DESCRIPTION, "name").`as`(MemberRecord::name.name),
            DSL.jsonGetAttribute(Member.MEMBER.DESCRIPTION, "url").`as`(WriterRecord::url.name)
        )
            .from(Member.MEMBER)
            .where(Member.MEMBER.EMAIL.eq(email))
            .and(Member.MEMBER.DELETED_AT.isNull)
            .fetchOneInto(MemberRecord::class.java)
            ?: throw IllegalArgumentException("cannot find member record by email: $email")
    }

    fun insertMember(command: InsertMemberCommand): Long {
        val result = dslContext.insertInto(Member.MEMBER)
            .set(Member.MEMBER.EMAIL, command.email)
            .set(Member.MEMBER.TYPE_CD, command.memberType.code)
            .returning(Member.MEMBER.ID)
            .fetchOne()

        return result?.getValue(Member.MEMBER.ID)
            ?: throw RuntimeException("Member with email ${command.email} insertion fail") // TODO: 에러 표준화
    }
}