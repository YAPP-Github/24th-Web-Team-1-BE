package com.few.api.domain.workbook.usecase.dto

import com.few.api.web.support.WorkBookCategory

data class BrowseWorkbooksUseCaseIn(
    val category: WorkBookCategory,
)