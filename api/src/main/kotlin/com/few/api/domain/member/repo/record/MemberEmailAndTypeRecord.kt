package com.few.api.domain.member.repo.record

import com.few.api.domain.common.vo.MemberType

data class MemberEmailAndTypeRecord(
    val email: String,
    val memberType: MemberType,
)