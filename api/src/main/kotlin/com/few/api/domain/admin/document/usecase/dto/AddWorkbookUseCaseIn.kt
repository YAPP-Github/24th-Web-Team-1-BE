package com.few.api.domain.admin.document.usecase.dto

import java.net.URL

data class AddWorkbookUseCaseIn(
    val title: String,
    val mainImageUrl: URL,
    val category: String,
    val description: String,
)