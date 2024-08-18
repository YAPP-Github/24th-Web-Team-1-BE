package com.few.batch.service.article.writer.service

import jooq.jooq_dsl.tables.MappingWorkbookArticle
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Service

@Service
class BrowseWorkbookLastDayColService(
    private val dslContext: DSLContext,
) {
    /** 워크북의 마지막 아티클의 day_col을 조회한다 */
    fun execute(workbookIds: Set<Long>): Map<Long, Int> {
        return dslContext.select(
            MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID,
            DSL.max(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.DAY_COL)
        )
            .from(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE)
            .where(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID.`in`(workbookIds))
            .and(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.DELETED_AT.isNull)
            .groupBy(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID)
            .fetch()
            .intoMap(
                MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID,
                DSL.max(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.DAY_COL)
            )
    }
}