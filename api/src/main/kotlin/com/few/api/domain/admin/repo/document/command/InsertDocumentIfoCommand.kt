package com.few.api.domain.admin.repo.document.command

import java.net.URL

data class InsertDocumentIfoCommand(
    val path: String,
    val url: URL,
    val alias: String = "",
)