package com.few.api.domain.member.repo.record

import java.net.URL

data class WriterRecordMappedWorkbook(
    val workbookId: Long,
    val writerId: Long,
    val name: String,
    val url: URL,
)