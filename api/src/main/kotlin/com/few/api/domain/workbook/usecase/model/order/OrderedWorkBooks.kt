package com.few.api.domain.workbook.usecase.model.order

import com.few.api.domain.workbook.usecase.model.WorkBooks

class OrderedWorkBooks(
    orderTargetWorkBooks: OrderTargetWorkBooks,
) : OrderAbleWorkBooks(orderTargetWorkBooks) {
    val orderedWorkbooks: WorkBooks = orderTargetWorkBooks.workbooks
}