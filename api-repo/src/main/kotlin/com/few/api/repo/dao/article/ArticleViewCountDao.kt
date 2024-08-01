package com.few.api.repo.dao.article

import com.few.api.repo.dao.article.command.ArticleViewCountCommand
import com.few.api.repo.dao.article.query.ArticleViewCountQuery
import com.few.api.repo.dao.article.query.SelectArticlesOrderByViewsQuery
import com.few.api.repo.dao.article.query.SelectRankByViewsQuery
import com.few.api.repo.dao.article.record.SelectArticleViewsRecord
import jooq.jooq_dsl.tables.ArticleViewCount.ARTICLE_VIEW_COUNT
import org.jooq.DSLContext
import org.jooq.Record2
import org.jooq.SelectLimitPercentStep
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
        .select(field("row_rank_tb.offset", Long::class.java))
        .from(
            dslContext
                .select(
                    ARTICLE_VIEW_COUNT.ARTICLE_ID,
                    rowNumber().over(orderBy(ARTICLE_VIEW_COUNT.VIEW_COUNT.desc())).`as`("offset")
                )
                .from(ARTICLE_VIEW_COUNT)
                .asTable("row_rank_tb")
        )
        .where(field("row_rank_tb.${ARTICLE_VIEW_COUNT.ARTICLE_ID.name}")!!.eq(query.articleId))
        .query

    fun selectArticlesOrderByViews(query: SelectArticlesOrderByViewsQuery): Set<SelectArticleViewsRecord> {
        return selectArticlesOrderByViewsQuery(query)
            .fetchInto(SelectArticleViewsRecord::class.java)
            .toSet()
    }

    fun selectArticlesOrderByViewsQuery(query: SelectArticlesOrderByViewsQuery): SelectLimitPercentStep<Record2<Any, Any>> {
        val articleViewCountOffsetTb = select()
            .from(ARTICLE_VIEW_COUNT)
            .where(ARTICLE_VIEW_COUNT.DELETED_AT.isNull)
            .orderBy(ARTICLE_VIEW_COUNT.VIEW_COUNT.desc())
            .limit(query.offset, Long.MAX_VALUE)
            .asTable("article_view_count_offset_tb")

        val baseQuery = dslContext.select(
            field("article_view_count_offset_tb.article_id").`as`(SelectArticleViewsRecord::articleId.name),
            field("article_view_count_offset_tb.view_count").`as`(SelectArticleViewsRecord::views.name)
        )
            .from(articleViewCountOffsetTb)

        return if (query.category != null) {
            baseQuery.where(field("article_view_count_offset_tb.category_cd").eq(query.category.code))
                .limit(10)
        } else {
            baseQuery
                .limit(10)
        } // TODO: .query로 리턴하면 리턴 타입이 달라져 못받는 문제
    }
}