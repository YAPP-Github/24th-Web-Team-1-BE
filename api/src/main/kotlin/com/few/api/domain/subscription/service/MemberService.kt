package com.few.api.domain.subscription.service

import com.few.api.domain.subscription.service.dto.InsertMemberInDto
import com.few.api.domain.subscription.service.dto.ReadMemberIdInDto
import com.few.api.exception.common.InsertException
import com.few.api.repo.dao.member.MemberDao
import com.few.api.repo.dao.member.command.InsertMemberCommand
import com.few.api.repo.dao.member.query.SelectMemberByEmailQuery
import com.few.api.repo.dao.member.record.MemberIdRecord
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberDao: MemberDao
) {

    fun readMemberId(dto: ReadMemberIdInDto): MemberIdRecord? {
        return memberDao.selectMemberByEmail(SelectMemberByEmailQuery(dto.email))
    }

    fun insertMember(dto: InsertMemberInDto): Long {
        return memberDao.insertMember(InsertMemberCommand(dto.email, dto.memberType)) ?: throw InsertException("member.insertfail.record")
    }
}