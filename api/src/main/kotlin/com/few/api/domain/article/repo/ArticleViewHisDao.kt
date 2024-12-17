package com.few.api.domain.article.repo

import com.few.api.domain.article.repo.command.ArticleViewHisCommand
import com.few.api.domain.article.repo.query.ArticleViewHisCountQuery
import jooq.jooq_dsl.tables.ArticleViewHis
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class ArticleViewHisDao(
    private val dslContext: DSLContext,
) {
    fun insertArticleViewHis(command: ArticleViewHisCommand) {
        insertArticleViewHisCommand(command).execute()
    }

    fun insertArticleViewHisCommand(command: ArticleViewHisCommand) =
        dslContext
            .insertInto(
                ArticleViewHis.ARTICLE_VIEW_HIS,
                ArticleViewHis.ARTICLE_VIEW_HIS.ARTICLE_MST_ID,
                ArticleViewHis.ARTICLE_VIEW_HIS.MEMBER_ID,
            ).values(
                command.articleId,
                command.memberId,
            )

    fun countArticleViews(query: ArticleViewHisCountQuery): Long? =
        countArticleViewsQuery(query)
            .fetchOne(0, Long::class.java)

    fun countArticleViewsQuery(query: ArticleViewHisCountQuery) =
        dslContext
            .selectCount()
            .from(ArticleViewHis.ARTICLE_VIEW_HIS)
            .where(ArticleViewHis.ARTICLE_VIEW_HIS.ARTICLE_MST_ID.eq(query.articleId))
            .query
}