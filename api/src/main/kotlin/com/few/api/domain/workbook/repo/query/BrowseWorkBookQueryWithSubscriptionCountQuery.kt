package com.few.api.domain.workbook.repo.query

data class BrowseWorkBookQueryWithSubscriptionCountQuery(
    /**
     * @see com.few.api.web.support.WorkBookCategory
     */
    val category: Byte,
)