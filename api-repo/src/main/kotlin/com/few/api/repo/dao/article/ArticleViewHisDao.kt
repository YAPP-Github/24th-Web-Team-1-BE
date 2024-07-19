package com.few.api.repo.dao.article

import com.few.api.repo.dao.article.command.ArticleViewHisCommand
import com.few.api.repo.dao.article.query.ArticleViewHisCountQuery
import jooq.jooq_dsl.tables.ArticleViewHis
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class ArticleViewHisDao(
    private val dslContext: DSLContext,
) {

    fun insertArticleViewHis(command: ArticleViewHisCommand) {
        dslContext.insertInto(
            ArticleViewHis.ARTICLE_VIEW_HIS,
            ArticleViewHis.ARTICLE_VIEW_HIS.ARTICLE_MST_ID,
            ArticleViewHis.ARTICLE_VIEW_HIS.MEMBER_ID
        ).values(
            command.articleId,
            command.memberId
        ).execute()
    }

    fun countArticleViews(query: ArticleViewHisCountQuery): Long? {
        return dslContext.selectCount()
            .from(ArticleViewHis.ARTICLE_VIEW_HIS)
            .where(ArticleViewHis.ARTICLE_VIEW_HIS.ARTICLE_MST_ID.eq(query.articleId))
            .fetchOne(0, Long::class.java)
    }
}