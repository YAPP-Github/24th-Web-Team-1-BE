package com.few.api.repo.dao.member.record

import java.net.URL

data class MemberRecord(
    val memberId: Long,
    val imageUrl: URL,
    val writerUrl: URL,
    val writerName: String?,
)