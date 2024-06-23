package com.few.api.domain.member.service

import com.few.api.domain.member.service.dto.GetMemberIdDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService {

    @Transactional(readOnly = true)
    fun getMemberId(dto: GetMemberIdDto): Long {
        return 1L // TODO: implemenets
    }
}