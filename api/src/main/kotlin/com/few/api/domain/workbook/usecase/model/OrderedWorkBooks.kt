package com.few.api.domain.workbook.usecase.model

class OrderedWorkBooks(
    val workbooks: List<WorkBook>,
) : WorkBooks(workbooks)