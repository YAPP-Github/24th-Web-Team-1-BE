package com.few.api.repo.dao.member.record

import java.net.URL

data class MemberRecord(
    val memberId: Long,
    val name: String,
    val url: URL
)