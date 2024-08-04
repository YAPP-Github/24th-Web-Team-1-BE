package com.few.api.repo.dao.member.record

import java.net.URL

data class MemberRecord(
    val memberId: Long,
    val writerName: String?, // writer only
    val imageUrl: URL?, // writer only
    val url: URL?, // writer only
)