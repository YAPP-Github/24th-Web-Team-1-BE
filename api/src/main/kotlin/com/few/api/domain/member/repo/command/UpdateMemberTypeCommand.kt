package com.few.api.domain.member.repo.command

import com.few.api.domain.common.vo.MemberType

data class UpdateMemberTypeCommand(
    val id: Long,
    val memberType: MemberType,
)