package com.few.api.domain.member.repo.record

import java.net.URL

data class MemberRecord(
    val memberId: Long,
    val writerName: String?, // writer only
    val imageUrl: URL?, // writer only
    val url: URL?, // writer only
)