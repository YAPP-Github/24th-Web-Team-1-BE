package com.few.api.repo.dao.member.record

import java.net.URL

data class WriterRecordMappedWorkbook(
    val workbookId: Long,
    val writerId: Long,
    val name: String,
    val url: URL,
)