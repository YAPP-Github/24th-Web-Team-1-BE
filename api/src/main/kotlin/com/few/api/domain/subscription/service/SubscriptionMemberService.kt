package com.few.api.domain.subscription.service

import com.few.api.domain.subscription.service.dto.*
import com.few.api.exception.common.InsertException
import com.few.api.repo.dao.member.MemberDao
import com.few.api.repo.dao.member.command.InsertMemberCommand
import com.few.api.repo.dao.member.query.SelectMemberByEmailQuery
import com.few.api.repo.dao.member.query.SelectMemberEmailQuery
import org.springframework.stereotype.Service

@Service
class SubscriptionMemberService(
    private val memberDao: MemberDao,
) {

    fun readMemberId(dto: ReadMemberIdInDto): MemberIdOutDto? {
        return memberDao.selectMemberByEmail(SelectMemberByEmailQuery(dto.email))?.let { MemberIdOutDto(it.memberId) }
    }

    fun insertMember(dto: InsertMemberInDto): MemberIdOutDto {
        return memberDao.insertMember(InsertMemberCommand(dto.email, dto.memberType))?.let { MemberIdOutDto(it) }
            ?: throw InsertException("member.insertfail.record")
    }

    fun readMemberEmail(dto: ReadMemberEmailInDto): ReadMemberEmailOutDto? {
        return SelectMemberEmailQuery(dto.memberId).let { query ->
            memberDao.selectMemberEmail(query)
        }?.let {
            ReadMemberEmailOutDto(it)
        }
    }
}