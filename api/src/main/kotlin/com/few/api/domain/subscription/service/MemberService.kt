package com.few.api.domain.subscription.service

import com.few.api.domain.subscription.service.dto.GetMemberIdDto
import org.springframework.stereotype.Service

@Service
class MemberService {

    fun getMemberId(dto: GetMemberIdDto): Long {
        return 1L // TODO: implemenets
    }
}