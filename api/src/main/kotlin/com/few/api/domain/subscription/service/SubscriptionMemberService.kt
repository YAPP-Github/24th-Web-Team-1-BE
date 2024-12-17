package com.few.api.domain.subscription.service

import com.few.api.domain.common.exception.InsertException
import com.few.api.domain.member.repo.MemberDao
import com.few.api.domain.member.repo.command.InsertMemberCommand
import com.few.api.domain.member.repo.query.SelectMemberByEmailQuery
import com.few.api.domain.member.repo.query.SelectMemberEmailQuery
import com.few.api.domain.subscription.service.dto.*
import org.springframework.stereotype.Service

@Service
class SubscriptionMemberService(
    private val memberDao: MemberDao,
) {
    fun readMemberId(dto: ReadMemberIdInDto): MemberIdOutDto? =
        memberDao.selectMemberByEmail(SelectMemberByEmailQuery(dto.email))?.let { MemberIdOutDto(it.memberId) }

    fun insertMember(dto: InsertMemberInDto): MemberIdOutDto =
        memberDao.insertMember(InsertMemberCommand(dto.email, dto.memberType))?.let { MemberIdOutDto(it) }
            ?: throw InsertException("member.insertfail.record")

    fun readMemberEmail(dto: ReadMemberEmailInDto): ReadMemberEmailOutDto? =
        memberDao.selectMemberEmail(SelectMemberEmailQuery(dto.memberId))?.let {
            ReadMemberEmailOutDto(it)
        }
}