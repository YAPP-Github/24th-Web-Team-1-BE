package com.few.api.domain.subscription.service

import com.few.api.domain.subscription.service.dto.InsertMemberInDto
import com.few.api.domain.subscription.service.dto.MemberIdOutDto
import com.few.api.domain.subscription.service.dto.ReadMemberIdInDto
import com.few.api.exception.common.InsertException
import com.few.api.repo.dao.member.MemberDao
import com.few.api.repo.dao.member.command.InsertMemberCommand
import com.few.api.repo.dao.member.query.SelectMemberByEmailQuery
import com.few.data.common.code.MEMBER_DEFAULT_IMG_URL
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberDao: MemberDao,
) {

    fun readMemberId(dto: ReadMemberIdInDto): MemberIdOutDto? {
        return memberDao.selectMemberByEmail(SelectMemberByEmailQuery(dto.email))?.let { MemberIdOutDto(it.memberId) }
    }

    fun insertMember(dto: InsertMemberInDto): MemberIdOutDto {
        return memberDao.insertMember(InsertMemberCommand(dto.email, dto.memberType, MEMBER_DEFAULT_IMG_URL))
            ?.let { MemberIdOutDto(it) }
            ?: throw InsertException("member.insertfail.record")
    }
}