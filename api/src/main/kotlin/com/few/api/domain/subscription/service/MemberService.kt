package com.few.api.domain.subscription.service

import com.few.api.domain.subscription.service.dto.readMemberIdDto
import org.springframework.stereotype.Service

@Service
class MemberService {

    fun readMemberId(dto: readMemberIdDto): Long {
        return 1L // TODO: implemenets
    }
}