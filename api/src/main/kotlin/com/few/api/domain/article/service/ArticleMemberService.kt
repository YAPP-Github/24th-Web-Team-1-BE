package com.few.api.domain.article.service

import com.few.api.domain.article.service.dto.ReadMemberByEmailDto
import com.few.api.domain.member.repo.MemberDao
import com.few.api.domain.member.repo.query.SelectMemberByEmailQuery
import org.springframework.stereotype.Service

@Service
class ArticleMemberService(
    private val memberDao: MemberDao,
) {

    fun readMemberByEmail(dto: ReadMemberByEmailDto): Long? {
        return memberDao.selectMemberByEmail(
            SelectMemberByEmailQuery(dto.email)
        )?.memberId
    }
}