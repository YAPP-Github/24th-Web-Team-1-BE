package com.few.api.repo.dao.article

import com.few.api.repo.dao.article.command.ArticleViewCountCommand
import com.few.api.repo.dao.article.query.ArticleViewCountQuery
import com.few.api.repo.dao.article.query.SelectArticlesOrderByViewsQuery
import com.few.api.repo.dao.article.query.SelectRankByViewsQuery
import com.few.api.repo.dao.article.record.SelectArticleViewsRecord
import jooq.jooq_dsl.tables.ArticleViewCount.ARTICLE_VIEW_COUNT
import org.jooq.DSLContext
import org.jooq.Record2
import org.jooq.SelectQuery
import org.jooq.impl.DSL.*
import org.springframework.stereotype.Repository

@Repository
class ArticleViewCountDao(
    private val dslContext: DSLContext,
) {

    fun upsertArticleViewCount(query: ArticleViewCountQuery) {
        upsertArticleViewCountQuery(query)
            .execute()
    }

    fun upsertArticleViewCountQuery(query: ArticleViewCountQuery) =
        dslContext.insertInto(ARTICLE_VIEW_COUNT)
            .set(ARTICLE_VIEW_COUNT.ARTICLE_ID, query.articleId)
            .set(ARTICLE_VIEW_COUNT.VIEW_COUNT, 1)
            .set(ARTICLE_VIEW_COUNT.CATEGORY_CD, query.categoryType.code)
            .onDuplicateKeyUpdate()
            .set(ARTICLE_VIEW_COUNT.VIEW_COUNT, ARTICLE_VIEW_COUNT.VIEW_COUNT.plus(1))

    fun insertArticleViewCountToZero(query: ArticleViewCountQuery) = insertArticleViewCountToZeroQuery(query).execute()

    fun insertArticleViewCountToZeroQuery(query: ArticleViewCountQuery) = dslContext.insertInto(ARTICLE_VIEW_COUNT)
        .set(ARTICLE_VIEW_COUNT.ARTICLE_ID, query.articleId)
        .set(ARTICLE_VIEW_COUNT.VIEW_COUNT, 0)
        .set(ARTICLE_VIEW_COUNT.CATEGORY_CD, query.categoryType.code)

    fun selectArticleViewCount(command: ArticleViewCountCommand): Long? {
        return selectArticleViewCountQuery(command).fetchOneInto(Long::class.java)
    }

    fun selectArticleViewCountQuery(command: ArticleViewCountCommand) = dslContext.select(
        ARTICLE_VIEW_COUNT.VIEW_COUNT
    ).from(ARTICLE_VIEW_COUNT)
        .where(ARTICLE_VIEW_COUNT.ARTICLE_ID.eq(command.articleId))
        .and(ARTICLE_VIEW_COUNT.DELETED_AT.isNull)
        .query

    fun selectRankByViews(query: SelectRankByViewsQuery): Long? {
        return selectRankByViewsQuery(query)
            .fetchOneInto(Long::class.java)
    }

    fun selectRankByViewsQuery(query: SelectRankByViewsQuery) = dslContext
        .select(field("offset"))
        .from(
            dslContext.select(
                ARTICLE_VIEW_COUNT.ARTICLE_ID,
                rowNumber().over(orderBy(ARTICLE_VIEW_COUNT.VIEW_COUNT.desc())).`as`("offset")
            ).from(ARTICLE_VIEW_COUNT.`as`("RankedRows"))
        )
        .where(field("RankedRows.article_id").eq(query.articleId))
        .query

    fun selectArticlesOrderByViews(query: SelectArticlesOrderByViewsQuery): Set<SelectArticleViewsRecord> {
        return selectArticlesOrderByViewsQuery(query)
            .fetchInto(SelectArticleViewsRecord::class.java)
            .toSet()
    }

    fun selectArticlesOrderByViewsQuery(query: SelectArticlesOrderByViewsQuery): SelectQuery<Record2<Long, Long>> {
        val articleViewCountOffsetTb = select()
            .from(ARTICLE_VIEW_COUNT)
            .where(ARTICLE_VIEW_COUNT.DELETED_AT.isNull)
            .orderBy(ARTICLE_VIEW_COUNT.VIEW_COUNT.desc())
            .limit(query.offset, Long.MAX_VALUE)
            .asTable("article_view_count_offset_tb")

        val sql = dslContext.select(
            articleViewCountOffsetTb.field(ARTICLE_VIEW_COUNT.ARTICLE_ID.`as`(SelectArticleViewsRecord::articleId.name)),
            articleViewCountOffsetTb.field(ARTICLE_VIEW_COUNT.VIEW_COUNT.`as`(SelectArticleViewsRecord::views.name))
        )
            .from(articleViewCountOffsetTb)

        if (query.category != null) {
            sql.where(field("article_view_count_offset_tb.category_cd").eq(query.category.code))
        }

        return sql.limit(10).query
    }
}