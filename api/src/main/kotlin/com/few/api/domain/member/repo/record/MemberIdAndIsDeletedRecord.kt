package com.few.api.domain.member.repo.record

data class MemberIdAndIsDeletedRecord(
    val memberId: Long,
    val isDeleted: Boolean,
)