package com.few.api.domain.article.repo

import com.few.api.domain.common.vo.CategoryType
import com.few.api.domain.article.repo.TempTable.ARTICLE_ID_COLUMN
import com.few.api.domain.article.repo.TempTable.ARTICLE_VIEW_COUNT_OFFSET_TABLE
import com.few.api.domain.article.repo.TempTable.ARTICLE_VIEW_COUNT_OFFSET_TABLE_ARTICLE_ID
import com.few.api.domain.article.repo.TempTable.ARTICLE_VIEW_COUNT_OFFSET_TABLE_CATEGORY_CD
import com.few.api.domain.article.repo.TempTable.ARTICLE_VIEW_COUNT_OFFSET_TABLE_VIEW_COUNT
import com.few.api.domain.article.repo.TempTable.EMAIL_VIEW_COUNT_TABLE
import com.few.api.domain.article.repo.TempTable.EMAIL_VIEW_COUNT_TABLE_ARTICLE_ID
import com.few.api.domain.article.repo.TempTable.EMAIL_VIEW_COUNT_TABLE_VIEW_COUNT
import com.few.api.domain.article.repo.TempTable.OFFSET_COLUMN
import com.few.api.domain.article.repo.TempTable.ROW_RANK_TABLE
import com.few.api.domain.article.repo.TempTable.ROW_RANK_TABLE_ARTICLE_ID
import com.few.api.domain.article.repo.TempTable.ROW_RANK_TABLE_OFFSET
import com.few.api.domain.article.repo.TempTable.TOTAL_VIEW_COUNT_TABLE
import com.few.api.domain.article.repo.TempTable.TOTAL_VIEW_COUNT_TABLE_ARTICLE_ID
import com.few.api.domain.article.repo.TempTable.TOTAL_VIEW_COUNT_TABLE_VIEW_COUNT
import com.few.api.domain.article.repo.TempTable.VIEW_COUNT_COLUMN
import com.few.api.domain.article.repo.command.ArticleViewCountCommand
import com.few.api.domain.article.repo.query.ArticleViewCountQuery
import com.few.api.domain.article.repo.query.SelectArticlesOrderByViewsQuery
import com.few.api.domain.article.repo.query.SelectRankByViewsQuery
import com.few.api.domain.article.repo.record.SelectArticleViewsRecord
import jooq.jooq_dsl.tables.ArticleViewCount.ARTICLE_VIEW_COUNT
import jooq.jooq_dsl.tables.SendArticleEventHistory.SEND_ARTICLE_EVENT_HISTORY
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import org.springframework.stereotype.Repository

object TempTable {
    const val ARTICLE_ID_COLUMN = "ARTICLE_ID"
    const val VIEW_COUNT_COLUMN = "VIEW_COUNT"
    const val OFFSET_COLUMN = "OFFSET"
    const val CATEGORY_CD_COLUMN = "CATEGORY_CD"

    const val ROW_RANK_TABLE = "row_rank_tb"
    const val ROW_RANK_TABLE_ARTICLE_ID = "$ROW_RANK_TABLE.$ARTICLE_ID_COLUMN"
    const val ROW_RANK_TABLE_OFFSET = "$ROW_RANK_TABLE.$OFFSET_COLUMN"

    const val EMAIL_VIEW_COUNT_TABLE = "evc"
    const val EMAIL_VIEW_COUNT_TABLE_ARTICLE_ID = "$EMAIL_VIEW_COUNT_TABLE.$ARTICLE_ID_COLUMN"
    const val EMAIL_VIEW_COUNT_TABLE_VIEW_COUNT = "$EMAIL_VIEW_COUNT_TABLE.$VIEW_COUNT_COLUMN"

    const val TOTAL_VIEW_COUNT_TABLE = "tvc"
    const val TOTAL_VIEW_COUNT_TABLE_ARTICLE_ID = "$TOTAL_VIEW_COUNT_TABLE.$ARTICLE_ID_COLUMN"
    const val TOTAL_VIEW_COUNT_TABLE_VIEW_COUNT = "$TOTAL_VIEW_COUNT_TABLE.$VIEW_COUNT_COLUMN"

    const val ARTICLE_VIEW_COUNT_OFFSET_TABLE = "article_view_count_offset_tb"
    const val ARTICLE_VIEW_COUNT_OFFSET_TABLE_ARTICLE_ID = "$ARTICLE_VIEW_COUNT_OFFSET_TABLE.$ARTICLE_ID_COLUMN"
    const val ARTICLE_VIEW_COUNT_OFFSET_TABLE_VIEW_COUNT = "$ARTICLE_VIEW_COUNT_OFFSET_TABLE.$VIEW_COUNT_COLUMN"
    const val ARTICLE_VIEW_COUNT_OFFSET_TABLE_CATEGORY_CD = "$ARTICLE_VIEW_COUNT_OFFSET_TABLE.$CATEGORY_CD_COLUMN"
}

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
        .select(field(ROW_RANK_TABLE_OFFSET, Long::class.java))
        .from(
            dslContext.select(
                field(TOTAL_VIEW_COUNT_TABLE_ARTICLE_ID, Long::class.java).`as`(ARTICLE_ID_COLUMN),
                rowNumber().over(
                    orderBy(
                        field(TOTAL_VIEW_COUNT_TABLE_VIEW_COUNT).desc(),
                        field(TOTAL_VIEW_COUNT_TABLE_ARTICLE_ID).desc()
                    )
                ).`as`(OFFSET_COLUMN)
            ).from(
                dslContext.select(
                    ARTICLE_VIEW_COUNT.ARTICLE_ID,
                    ARTICLE_VIEW_COUNT.VIEW_COUNT.plus(
                        ifnull(field(EMAIL_VIEW_COUNT_TABLE_VIEW_COUNT, Long::class.java), 0)
                    ).`as`(VIEW_COUNT_COLUMN)
                ).from(ARTICLE_VIEW_COUNT)
                    .leftJoin(
                        dslContext.select(
                            SEND_ARTICLE_EVENT_HISTORY.ARTICLE_ID,
                            count(SEND_ARTICLE_EVENT_HISTORY.ARTICLE_ID).`as`(VIEW_COUNT_COLUMN)
                        ).from(
                            SEND_ARTICLE_EVENT_HISTORY
                        ).groupBy(
                            SEND_ARTICLE_EVENT_HISTORY.ARTICLE_ID
                        ).asTable(EMAIL_VIEW_COUNT_TABLE)
                    ).on(
                        ARTICLE_VIEW_COUNT.ARTICLE_ID.eq(
                            field(EMAIL_VIEW_COUNT_TABLE_ARTICLE_ID, Long::class.java)
                        )
                    )
                    .asTable(TOTAL_VIEW_COUNT_TABLE)
            ).asTable(ROW_RANK_TABLE)
        ).where(field(ROW_RANK_TABLE_ARTICLE_ID).eq(query.articleId))
        .query

    fun selectArticlesOrderByViews(query: SelectArticlesOrderByViewsQuery): List<SelectArticleViewsRecord> {
        return selectArticlesOrderByViewsQuery(query)
            .fetchInto(SelectArticleViewsRecord::class.java)
    }

    fun selectArticlesOrderByViewsQuery(query: SelectArticlesOrderByViewsQuery) = dslContext
        .select(
            field(ARTICLE_VIEW_COUNT_OFFSET_TABLE_ARTICLE_ID).`as`(SelectArticleViewsRecord::articleId.name),
            field(ARTICLE_VIEW_COUNT_OFFSET_TABLE_VIEW_COUNT).`as`(SelectArticleViewsRecord::views.name)
        ).from(
            dslContext.select(
                ARTICLE_VIEW_COUNT.ARTICLE_ID,
                ARTICLE_VIEW_COUNT.VIEW_COUNT.plus(
                    ifnull(field(EMAIL_VIEW_COUNT_TABLE_VIEW_COUNT, Long::class.java), 0)
                ).`as`(VIEW_COUNT_COLUMN),
                ARTICLE_VIEW_COUNT.CATEGORY_CD
            ).from(ARTICLE_VIEW_COUNT)
                .leftJoin(
                    dslContext.select(
                        SEND_ARTICLE_EVENT_HISTORY.ARTICLE_ID,
                        count(SEND_ARTICLE_EVENT_HISTORY.ARTICLE_ID).`as`(VIEW_COUNT_COLUMN)
                    ).from(
                        SEND_ARTICLE_EVENT_HISTORY
                    ).groupBy(
                        SEND_ARTICLE_EVENT_HISTORY.ARTICLE_ID
                    ).asTable(
                        EMAIL_VIEW_COUNT_TABLE
                    )
                ).on(
                    ARTICLE_VIEW_COUNT.ARTICLE_ID.eq(
                        field(
                            EMAIL_VIEW_COUNT_TABLE_ARTICLE_ID,
                            Long::class.java
                        )
                    )
                ).orderBy(
                    field(VIEW_COUNT_COLUMN, Long::class.java).desc(),
                    ARTICLE_VIEW_COUNT.ARTICLE_ID.desc()
                ).limit(query.offset, Long.MAX_VALUE)
                .asTable(ARTICLE_VIEW_COUNT_OFFSET_TABLE)
        ).where(
            when {
                (query.category == CategoryType.All) -> noCondition()
                else -> field(ARTICLE_VIEW_COUNT_OFFSET_TABLE_CATEGORY_CD).eq(query.category.code)
            }
        ).limit(11)
        .query
}