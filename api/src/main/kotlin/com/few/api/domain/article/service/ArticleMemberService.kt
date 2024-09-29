package com.few.api.domain.article.service

import com.few.api.domain.article.service.dto.ReadMemberByEmailDto
import com.few.api.repo.dao.member.MemberDao
import com.few.api.repo.dao.member.query.SelectMemberByEmailQuery
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