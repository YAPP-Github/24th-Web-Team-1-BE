package com.few.api.repo.dao.article

import com.few.api.repo.config.LocalCacheConfig.Companion.LOCAL_CM
import com.few.api.repo.config.LocalCacheConfig.Companion.SELECT_ARTICLE_RECORD_CACHE
import com.few.api.repo.dao.article.command.InsertFullArticleRecordCommand
import com.few.api.repo.dao.article.query.SelectArticleIdByWorkbookIdAndDayQuery
import com.few.api.repo.dao.article.query.SelectArticleRecordQuery
import com.few.api.repo.dao.article.query.SelectWorkBookArticleRecordQuery
import com.few.api.repo.dao.article.query.SelectWorkbookMappedArticleRecordsQuery
import com.few.api.repo.dao.article.record.SelectArticleRecord
import com.few.api.repo.dao.article.record.SelectWorkBookArticleRecord
import com.few.api.repo.dao.article.record.SelectWorkBookMappedArticleRecord
import jooq.jooq_dsl.tables.ArticleIfo
import jooq.jooq_dsl.tables.ArticleMst
import jooq.jooq_dsl.tables.MappingWorkbookArticle
import org.jooq.*
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository

@Repository
class ArticleDao(
    private val dslContext: DSLContext,
) {

    @Cacheable(key = "#query.articleId", cacheManager = LOCAL_CM, cacheNames = [SELECT_ARTICLE_RECORD_CACHE])
    fun selectArticleRecord(query: SelectArticleRecordQuery): SelectArticleRecord? {
        return selectArticleRecordQuery(query)
            .fetchOneInto(SelectArticleRecord::class.java)
    }

    fun selectArticleRecordQuery(query: SelectArticleRecordQuery) = dslContext.select(
        ArticleMst.ARTICLE_MST.ID.`as`(SelectArticleRecord::articleId.name),
        ArticleMst.ARTICLE_MST.MEMBER_ID.`as`(SelectArticleRecord::writerId.name),
        ArticleMst.ARTICLE_MST.MAIN_IMAGE_URL.`as`(SelectArticleRecord::mainImageURL.name),
        ArticleMst.ARTICLE_MST.TITLE.`as`(SelectArticleRecord::title.name),
        ArticleMst.ARTICLE_MST.CATEGORY_CD.`as`(SelectArticleRecord::category.name),
        ArticleIfo.ARTICLE_IFO.CONTENT.`as`(SelectArticleRecord::content.name),
        ArticleMst.ARTICLE_MST.CREATED_AT.`as`(SelectArticleRecord::createdAt.name)
    ).from(ArticleMst.ARTICLE_MST)
        .join(ArticleIfo.ARTICLE_IFO)
        .on(ArticleMst.ARTICLE_MST.ID.eq(ArticleIfo.ARTICLE_IFO.ARTICLE_MST_ID))
        .where(ArticleMst.ARTICLE_MST.ID.eq(query.articleId))
        .and(ArticleMst.ARTICLE_MST.DELETED_AT.isNull)
        .query

    fun selectWorkBookArticleRecord(query: SelectWorkBookArticleRecordQuery): SelectWorkBookArticleRecord? {
        return selectWorkBookArticleRecordQuery(query)
            .fetchOneInto(SelectWorkBookArticleRecord::class.java)
    }

    fun selectWorkBookArticleRecordQuery(query: SelectWorkBookArticleRecordQuery) =
        dslContext.select(
            ArticleMst.ARTICLE_MST.ID.`as`(SelectWorkBookArticleRecord::articleId.name),
            ArticleMst.ARTICLE_MST.MEMBER_ID.`as`(SelectWorkBookArticleRecord::writerId.name),
            ArticleMst.ARTICLE_MST.MAIN_IMAGE_URL.`as`(SelectWorkBookArticleRecord::mainImageURL.name),
            ArticleMst.ARTICLE_MST.TITLE.`as`(SelectWorkBookArticleRecord::title.name),
            ArticleMst.ARTICLE_MST.CATEGORY_CD.`as`(SelectWorkBookArticleRecord::category.name),
            ArticleIfo.ARTICLE_IFO.CONTENT.`as`(SelectWorkBookArticleRecord::content.name),
            ArticleMst.ARTICLE_MST.CREATED_AT.`as`(SelectWorkBookArticleRecord::createdAt.name),
            MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.DAY_COL.`as`(SelectWorkBookArticleRecord::day.name)
        ).from(ArticleMst.ARTICLE_MST)
            .join(ArticleIfo.ARTICLE_IFO)
            .on(ArticleMst.ARTICLE_MST.ID.eq(ArticleIfo.ARTICLE_IFO.ARTICLE_MST_ID))
            .join(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE)
            .on(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID.eq(query.workbookId))
            .and(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.ARTICLE_ID.eq(ArticleMst.ARTICLE_MST.ID))
            .where(ArticleMst.ARTICLE_MST.ID.eq(query.articleId))
            .and(ArticleMst.ARTICLE_MST.DELETED_AT.isNull)
            .query

    fun selectWorkbookMappedArticleRecords(query: SelectWorkbookMappedArticleRecordsQuery): List<SelectWorkBookMappedArticleRecord> {
        return selectWorkbookMappedArticleRecordsQuery(query)
            .fetchInto(SelectWorkBookMappedArticleRecord::class.java)
    }

    fun selectWorkbookMappedArticleRecordsQuery(query: SelectWorkbookMappedArticleRecordsQuery) = dslContext.select(
        ArticleMst.ARTICLE_MST.ID.`as`(SelectWorkBookArticleRecord::articleId.name),
        ArticleMst.ARTICLE_MST.MEMBER_ID.`as`(SelectWorkBookArticleRecord::writerId.name),
        ArticleMst.ARTICLE_MST.MAIN_IMAGE_URL.`as`(SelectWorkBookArticleRecord::mainImageURL.name),
        ArticleMst.ARTICLE_MST.TITLE.`as`(SelectWorkBookArticleRecord::title.name),
        ArticleMst.ARTICLE_MST.CATEGORY_CD.`as`(SelectWorkBookArticleRecord::category.name),
        ArticleIfo.ARTICLE_IFO.CONTENT.`as`(SelectWorkBookArticleRecord::content.name),
        ArticleMst.ARTICLE_MST.CREATED_AT.`as`(SelectWorkBookArticleRecord::createdAt.name),
        MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.DAY_COL.`as`(SelectWorkBookArticleRecord::day.name)
    )
        .from(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE)
        .leftJoin(ArticleMst.ARTICLE_MST)
        .on(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.ARTICLE_ID.eq(ArticleMst.ARTICLE_MST.ID))
        .join(ArticleIfo.ARTICLE_IFO)
        .on(ArticleMst.ARTICLE_MST.ID.eq(ArticleIfo.ARTICLE_IFO.ARTICLE_MST_ID))
        .where(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID.eq(query.workbookId))
        .and(ArticleMst.ARTICLE_MST.DELETED_AT.isNull)
        .query

    fun insertFullArticleRecord(command: InsertFullArticleRecordCommand): Long {
        val mstId = insertArticleMstCommand(command)
            .returning(ArticleMst.ARTICLE_MST.ID)
            .fetchOne()
        insertArticleIfoCommand(mstId!!.getValue(ArticleMst.ARTICLE_MST.ID), command).execute()
        return mstId.getValue(ArticleMst.ARTICLE_MST.ID)
    }

    fun insertArticleMstCommand(command: InsertFullArticleRecordCommand) =
        dslContext.insertInto(ArticleMst.ARTICLE_MST)
            .set(ArticleMst.ARTICLE_MST.MEMBER_ID, command.writerId)
            .set(ArticleMst.ARTICLE_MST.MAIN_IMAGE_URL, command.mainImageURL.toString())
            .set(ArticleMst.ARTICLE_MST.TITLE, command.title)
            .set(ArticleMst.ARTICLE_MST.CATEGORY_CD, command.category)

    fun insertArticleIfoCommand(
        mstId: Long,
        command: InsertFullArticleRecordCommand,
    ) = dslContext.insertInto(ArticleIfo.ARTICLE_IFO)
        .set(ArticleIfo.ARTICLE_IFO.ARTICLE_MST_ID, mstId)
        .set(ArticleIfo.ARTICLE_IFO.CONTENT, command.content)

    fun selectArticleIdByWorkbookIdAndDay(query: SelectArticleIdByWorkbookIdAndDayQuery): Long? {
        return selectArticleIdByWorkbookIdAndDayQuery(query)
            .fetchOneInto(Long::class.java)
    }

    fun selectArticleIdByWorkbookIdAndDayQuery(query: SelectArticleIdByWorkbookIdAndDayQuery) =
        dslContext.select(
            MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.ARTICLE_ID
        ).from(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE)
            .where(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID.eq(query.workbookId))
            .and(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.DAY_COL.eq(query.day))
            .query
}