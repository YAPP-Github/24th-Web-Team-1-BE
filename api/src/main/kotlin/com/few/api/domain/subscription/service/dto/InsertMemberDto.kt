package com.few.api.domain.subscription.service.dto

import com.few.data.common.code.MemberType

data class InsertMemberDto(
    val email: String,
    val memberType: MemberType
)