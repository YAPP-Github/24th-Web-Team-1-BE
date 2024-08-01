package com.few.api.repo.dao.member.record

import com.few.data.common.code.MemberType

data class MemberEmailAndTypeRecord(
    val email: String,
    val memberType: MemberType,
)