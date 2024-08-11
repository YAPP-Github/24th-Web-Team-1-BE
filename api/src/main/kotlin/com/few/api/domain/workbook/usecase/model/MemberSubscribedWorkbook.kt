package com.few.api.domain.workbook.usecase.model

data class MemberSubscribedWorkbook(
    val workbookId: Long,
    val isActiveSub: Boolean,
    val currentDay: Int,
)