package com.few.api.repo.dao.article

import com.few.api.repo.dao.article.query.SelectArticleRecordQuery
import com.few.api.repo.dao.article.record.SelectArticleRecord
import jooq.jooq_dsl.tables.ArticleIfo
import jooq.jooq_dsl.tables.ArticleMst
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class ArticleDao(
    private val dslContext: DSLContext
) {
    fun selectArticleRecord(query: SelectArticleRecordQuery): SelectArticleRecord {
        val articleId = query.articleId

        return dslContext.select(
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
            .where(ArticleMst.ARTICLE_MST.ID.eq(articleId))
            .fetchOneInto(SelectArticleRecord::class.java)
            ?: throw IllegalArgumentException("cannot find article record by articleId: $articleId")
    }
}