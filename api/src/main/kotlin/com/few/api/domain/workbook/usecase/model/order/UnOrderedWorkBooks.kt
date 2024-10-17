package com.few.api.domain.workbook.usecase.model.order

class UnOrderedWorkBooks(
    orderTargetWorkBooks: OrderTargetWorkBooks,
    private val delegator: WorkbookOrderDelegator,
) : OrderAbleWorkBooks(orderTargetWorkBooks) {
    fun order(): OrderedWorkBooks {
        val orderedWorkbooks = delegator.order(targetWorkBooks)
        return OrderedWorkBooks(orderedWorkbooks)
    }
}