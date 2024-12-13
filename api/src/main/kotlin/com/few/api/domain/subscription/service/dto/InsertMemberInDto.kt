package com.few.api.domain.subscription.service.dto

import com.few.api.domain.common.vo.MemberType

data class InsertMemberInDto(
    val email: String,
    val memberType: MemberType,
)