package com.few.api.repo.dao.workbook

import com.few.api.repo.dao.workbook.command.InsertWorkBookCommand
import com.few.api.repo.dao.workbook.command.MapWorkBookToArticleCommand
import com.few.api.repo.dao.workbook.query.SelectWorkBookRecordQuery
import com.few.api.repo.dao.workbook.record.SelectWorkBookRecord
import com.few.data.common.code.CategoryType
import jooq.jooq_dsl.tables.MappingWorkbookArticle
import jooq.jooq_dsl.tables.Workbook
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class WorkbookDao(
    private val dslContext: DSLContext,
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

    fun insertWorkBook(command: InsertWorkBookCommand): Long? {
        return dslContext.insertInto(Workbook.WORKBOOK)
            .set(Workbook.WORKBOOK.TITLE, command.title)
            .set(Workbook.WORKBOOK.MAIN_IMAGE_URL, command.mainImageUrl.toString())
            .set(Workbook.WORKBOOK.CATEGORY_CD, CategoryType.convertToCode(command.category))
            .set(Workbook.WORKBOOK.DESCRIPTION, command.description)
            .returning(Workbook.WORKBOOK.ID)
            .fetchOne()
            ?.id
    }

    fun mapWorkBookToArticle(command: MapWorkBookToArticleCommand) {
        dslContext.insertInto(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE)
            .set(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID, command.workbookId)
            .set(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.ARTICLE_ID, command.articleId)
            .set(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.DAY_COL, command.dayCol)
            .execute()
    }
}