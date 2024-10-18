package com.few.api.domain.member.usecase.dto

data class SaveMemberTxCaseOut(
    val headComment: String,
    val subComment: String,
    val memberId: Long,
)