package com.few.api.domain.subscription.service

import com.few.api.domain.subscription.service.dto.InsertMemberDto
import com.few.api.domain.subscription.service.dto.ReadMemberIdDto
import com.few.api.repo.dao.member.MemberDao
import com.few.api.repo.dao.member.command.InsertMemberCommand
import com.few.api.repo.dao.member.query.SelectMemberQuery
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberDao: MemberDao
) {

    fun readMemberId(dto: ReadMemberIdDto): Long? {
        return memberDao.selectMember(SelectMemberQuery(dto.email)).memberId
    }

    fun insertMember(dto: InsertMemberDto): Long {
        return memberDao.insertMember(InsertMemberCommand(dto.email, dto.memberType))
    }
}