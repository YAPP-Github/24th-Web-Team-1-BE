package com.few.api.repo.dao.document.command

import java.net.URL

data class InsertDocumentIfoCommand(
    val path: String,
    val url: URL,
    val alias: String = ""
)