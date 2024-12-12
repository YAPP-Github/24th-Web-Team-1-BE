package com.few.api.domain.member.repo.command

import com.few.api.domain.common.vo.MemberType

data class InsertMemberCommand(
    val email: String,
    val memberType: MemberType,
)