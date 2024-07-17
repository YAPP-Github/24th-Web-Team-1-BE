package com.few.api.repo.dao.article

import com.few.api.repo.dao.article.command.ArticleViewHisCommand
import jooq.jooq_dsl.tables.ArticleViewHis
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class ArticleViewHisDao(
    private val dslContext: DSLContext,
) {

    fun insertArticleViewHis(query: ArticleViewHisCommand) {
        dslContext.insertInto(
            ArticleViewHis.ARTICLE_VIEW_HIS,
            ArticleViewHis.ARTICLE_VIEW_HIS.ARTICLE_MST_ID,
            ArticleViewHis.ARTICLE_VIEW_HIS.MEMBER_ID
        ).values(
            query.articleId,
            query.memberId
        ).execute()
    }
}