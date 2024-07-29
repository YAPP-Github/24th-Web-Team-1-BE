package com.few.api.repo.dao.member.record

import com.few.data.common.code.MemberType

data class MemberIdAndTypeRecord(
    val memberId: Long,
    val memberType: MemberType,
)