package com.few.api.domain.article.repo

import com.few.api.domain.article.repo.query.*
import com.few.api.domain.article.repo.record.*
import com.few.api.domain.common.vo.MemberType
import com.few.api.config.ApiLocalCacheConfig.Companion.LOCAL_CM
import com.few.api.config.ApiLocalCacheConfig.Companion.SELECT_ARTICLE_RECORD_CACHE
import com.few.api.domain.article.repo.command.InsertFullArticleRecordCommand
import jooq.jooq_dsl.tables.*
import jooq.jooq_dsl.tables.MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE
import org.jooq.*
import org.jooq.impl.DSL
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
        .join(ArticleMst.ARTICLE_MST)
        .on(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.ARTICLE_ID.eq(ArticleMst.ARTICLE_MST.ID))
        .join(ArticleIfo.ARTICLE_IFO)
        .on(ArticleMst.ARTICLE_MST.ID.eq(ArticleIfo.ARTICLE_IFO.ARTICLE_MST_ID))
        .where(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID.eq(query.workbookId))
        .and(ArticleMst.ARTICLE_MST.DELETED_AT.isNull)
        .orderBy(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.DAY_COL)
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

    fun selectArticleContents(articleIds: Set<Long>): List<SelectArticleContentsRecord> =
        selectArticleContentsQuery(articleIds)
            .fetchInto(SelectArticleContentsRecord::class.java)

    fun selectArticleContentsQuery(articleIds: Set<Long>) = dslContext.select(
        ArticleIfo.ARTICLE_IFO.ARTICLE_MST_ID.`as`(SelectArticleContentsRecord::articleId.name),
        ArticleIfo.ARTICLE_IFO.CONTENT.`as`(SelectArticleContentsRecord::content.name)
    ).from(ArticleIfo.ARTICLE_IFO)
        .where(ArticleIfo.ARTICLE_IFO.ARTICLE_MST_ID.`in`(articleIds))
        .and(ArticleIfo.ARTICLE_IFO.DELETED_AT.isNull)

    fun selectArticleContent(query: SelectArticleContentQuery): ArticleContentRecord? {
        return selectArticleContentQuery(query)
            .fetchOneInto(ArticleContentRecord::class.java)
    }

    fun selectArticleContentQuery(query: SelectArticleContentQuery) =
        dslContext.select(
            ArticleIfo.ARTICLE_IFO.ARTICLE_MST_ID.`as`(ArticleContentRecord::id.name),
            ArticleIfo.ARTICLE_IFO.CONTENT.`as`(ArticleContentRecord::articleContent.name),
            ArticleMst.ARTICLE_MST.TITLE.`as`(ArticleContentRecord::articleTitle.name),
            ArticleMst.ARTICLE_MST.CATEGORY_CD.`as`(ArticleContentRecord::category.name),
            DSL.jsonGetAttributeAsText(Member.MEMBER.DESCRIPTION, "name")
                .`as`(ArticleContentRecord::writerName.name),
            DSL.jsonGetAttribute(Member.MEMBER.DESCRIPTION, "url")
                .`as`(ArticleContentRecord::writerLink.name)
        )
            .from(ArticleIfo.ARTICLE_IFO)
            .join(ArticleMst.ARTICLE_MST)
            .on(ArticleIfo.ARTICLE_IFO.ARTICLE_MST_ID.eq(ArticleMst.ARTICLE_MST.ID))
            .join(Member.MEMBER)
            .on(
                ArticleMst.ARTICLE_MST.MEMBER_ID.eq(Member.MEMBER.ID)
                    .and(Member.MEMBER.TYPE_CD.eq(MemberType.WRITER.code))
            )
            .where(ArticleIfo.ARTICLE_IFO.ARTICLE_MST_ID.eq(query.articleId))
            .and(ArticleIfo.ARTICLE_IFO.DELETED_AT.isNull)

    fun selectArticleIdsByWorkbookIdLimitDay(query: SelectAritlceIdByWorkbookIdAndDayQuery): ArticleIdRecord {
        return selectArticleIdByWorkbookIdLimitDayQuery(query)
            .fetch()
            .map { it[MAPPING_WORKBOOK_ARTICLE.ARTICLE_ID] }
            .let { ArticleIdRecord(it) }
    }

    fun selectArticleIdByWorkbookIdLimitDayQuery(query: SelectAritlceIdByWorkbookIdAndDayQuery) =
        dslContext
            .select(MAPPING_WORKBOOK_ARTICLE.ARTICLE_ID)
            .from(MAPPING_WORKBOOK_ARTICLE)
            .join(ArticleMst.ARTICLE_MST)
            .on(MAPPING_WORKBOOK_ARTICLE.ARTICLE_ID.eq(ArticleMst.ARTICLE_MST.ID))
            .where(MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID.eq(query.workbookId))
            .and(ArticleMst.ARTICLE_MST.DELETED_AT.isNull)
            .orderBy(MAPPING_WORKBOOK_ARTICLE.DAY_COL.asc())
            .limit(query.day)
            .query
}