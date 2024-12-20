package com.few.api.domain.batch.article.writer.service

import jooq.jooq_dsl.tables.Member
import org.jooq.DSLContext
import org.springframework.stereotype.Service

@Service
class BrowseMemberEmailService(
    private val dslContext: DSLContext,
) {
    /** 회원 ID를 기준으로 이메일을 조회한다.*/
    fun execute(memberIds: Set<Long>): Map<Long, String> =
        dslContext
            .select(
                Member.MEMBER.ID,
                Member.MEMBER.EMAIL,
            ).from(Member.MEMBER)
            .where(Member.MEMBER.ID.`in`(memberIds))
            .and(Member.MEMBER.DELETED_AT.isNull)
            .fetch()
            .intoMap(Member.MEMBER.ID, Member.MEMBER.EMAIL)
}