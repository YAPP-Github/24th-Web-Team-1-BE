package com.few.api.repo.dao.article

import com.few.api.repo.dao.article.query.SelectArticleRecordQuery
import com.few.api.repo.dao.article.query.SelectWorkBookArticleRecordQuery
import com.few.api.repo.dao.article.record.SelectArticleRecord
import com.few.api.repo.dao.article.record.SelectWorkBookArticleRecord
import jooq.jooq_dsl.tables.ArticleIfo
import jooq.jooq_dsl.tables.ArticleMst
import jooq.jooq_dsl.tables.MappingWorkbookArticle
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

    fun selectWorkBookArticleRecord(query: SelectWorkBookArticleRecordQuery): SelectWorkBookArticleRecord {
        val articleMst = ArticleMst.ARTICLE_MST
        val articleIfo = ArticleIfo.ARTICLE_IFO
        val mappingWorkbookArticle = MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE

        val articleId = query.articleId
        val workbookId = query.workbookId

        return dslContext.select(
            articleMst.ID.`as`(SelectWorkBookArticleRecord::articleId.name),
            articleMst.MEMBER_ID.`as`(SelectWorkBookArticleRecord::writerId.name),
            articleMst.MAIN_IMAGE_URL.`as`(SelectWorkBookArticleRecord::mainImageURL.name),
            articleMst.TITLE.`as`(SelectWorkBookArticleRecord::title.name),
            articleMst.CATEGORY_CD.`as`(SelectWorkBookArticleRecord::category.name),
            articleIfo.CONTENT.`as`(SelectWorkBookArticleRecord::content.name),
            articleMst.CREATED_AT.`as`(SelectWorkBookArticleRecord::createdAt.name),
            mappingWorkbookArticle.DAY_COL.`as`(SelectWorkBookArticleRecord::day.name)
        ).from(articleMst)
            .join(articleIfo)
            .on(articleMst.ID.eq(articleIfo.ARTICLE_MST_ID))
            .join(mappingWorkbookArticle)
            .on(mappingWorkbookArticle.WORKBOOK_ID.eq(workbookId))
            .and(mappingWorkbookArticle.ARTICLE_ID.eq(articleMst.ID))
            .where(articleMst.ID.eq(articleId))
            .fetchOneInto(SelectWorkBookArticleRecord::class.java)
            ?: throw IllegalArgumentException("cannot find $workbookId article record by articleId: $articleId")
    }
}