package com.few.api.repo.dao.member

import com.few.api.repo.dao.member.query.SelectWriterQuery
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
            .fetchOneInto(WriterRecord::class.java)
            ?: throw IllegalArgumentException("cannot find writer record by writerId: $writerId")
    }
}