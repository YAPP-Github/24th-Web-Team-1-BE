package com.few.api.repo.dao.workbook

import com.few.api.repo.dao.workbook.query.SelectWorkBookRecordQuery
import com.few.api.repo.dao.workbook.record.SelectWorkBookRecord
import jooq.jooq_dsl.tables.Workbook
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class WorkbookDao(
    private val dslContext: DSLContext
) {
    fun selectWorkBook(query: SelectWorkBookRecordQuery): SelectWorkBookRecord? {
        return dslContext.select(
            Workbook.WORKBOOK.ID.`as`(SelectWorkBookRecord::id.name),
            Workbook.WORKBOOK.TITLE.`as`(SelectWorkBookRecord::title.name),
            Workbook.WORKBOOK.MAIN_IMAGE_URL.`as`(SelectWorkBookRecord::mainImageUrl.name),
            Workbook.WORKBOOK.CATEGORY_CD.`as`(SelectWorkBookRecord::category.name),
            Workbook.WORKBOOK.DESCRIPTION.`as`(SelectWorkBookRecord::description.name),
            Workbook.WORKBOOK.CREATED_AT.`as`(SelectWorkBookRecord::createdAt.name)
        )
            .from(Workbook.WORKBOOK)
            .where(Workbook.WORKBOOK.ID.eq(query.id))
            .and(Workbook.WORKBOOK.DELETED_AT.isNull)
            .fetchOneInto(SelectWorkBookRecord::class.java)
    }
}