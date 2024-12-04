package com.few.api.domain.member.usecase.dto

import com.few.api.domain.member.repo.record.MemberIdAndIsDeletedRecord

data class SaveMemberTxCaseIn(
    val record: MemberIdAndIsDeletedRecord?,
    val email: String,
)