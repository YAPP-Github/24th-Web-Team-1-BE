package com.few.api.repo.dao.article

import com.few.api.repo.dao.article.command.ArticleViewCountCommand
import com.few.api.repo.dao.article.query.ArticleViewCountQuery
import jooq.jooq_dsl.Tables.ARTICLE_VIEW_COUNT
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class ArticleViewCountDao(
    private val dslContext: DSLContext,
) {

    fun upsertArticleViewCount(query: ArticleViewCountQuery) {
        dslContext.insertInto(ARTICLE_VIEW_COUNT)
            .set(ARTICLE_VIEW_COUNT.ARTICLE_ID, query.articleId)
            .set(ARTICLE_VIEW_COUNT.VIEW_COUNT, 1)
            .onDuplicateKeyUpdate()
            .set(ARTICLE_VIEW_COUNT.VIEW_COUNT, ARTICLE_VIEW_COUNT.VIEW_COUNT.plus(1))
    }

    fun selectArticleViewCount(command: ArticleViewCountCommand): Long? {
        return dslContext.select(
            ARTICLE_VIEW_COUNT.VIEW_COUNT
        ).from(ARTICLE_VIEW_COUNT)
            .where(ARTICLE_VIEW_COUNT.ARTICLE_ID.eq(command.articleId))
            .and(ARTICLE_VIEW_COUNT.DELETED_AT.isNull)
            .fetchOneInto(Long::class.java)
    }
}