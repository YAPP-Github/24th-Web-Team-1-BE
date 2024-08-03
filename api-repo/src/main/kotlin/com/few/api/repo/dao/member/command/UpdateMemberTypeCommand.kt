package com.few.api.repo.dao.member.command

import com.few.data.common.code.MemberType

data class UpdateMemberTypeCommand(
    val id: Long,
    val memberType: MemberType,
)