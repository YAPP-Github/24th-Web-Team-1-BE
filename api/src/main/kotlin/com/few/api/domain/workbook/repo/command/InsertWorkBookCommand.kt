package com.few.api.domain.workbook.repo.command

import java.net.URL

data class InsertWorkBookCommand(
    val title: String,
    val mainImageUrl: URL,
    val category: String,
    val description: String,
)