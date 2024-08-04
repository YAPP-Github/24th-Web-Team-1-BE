package com.few.api.repo.dao.article

import com.few.api.repo.dao.article.command.ArticleMainCardExcludeWorkbookCommand
import com.few.api.repo.dao.article.command.UpdateArticleMainCardWorkbookCommand
import com.few.api.repo.dao.article.record.ArticleMainCardRecord
import com.few.api.repo.dao.article.support.CommonJsonMapper
import com.few.api.repo.dao.article.support.ArticleMainCardMapper
import jooq.jooq_dsl.tables.ArticleMainCard.ARTICLE_MAIN_CARD
import org.jooq.*
import org.jooq.impl.DSL.*
import org.springframework.stereotype.Repository

@Repository
class ArticleMainCardDao(
    private val dslContext: DSLContext,
    private val commonJsonMapper: CommonJsonMapper,
    private val articleMainCardMapper: ArticleMainCardMapper,
) {

    fun selectArticleMainCardsRecord(articleIds: Set<Long>): Set<ArticleMainCardRecord> {
        return selectArticleMainCardsRecordQuery(articleIds)
            .fetch(articleMainCardMapper)
            .toSet()
    }

    private fun selectArticleMainCardsRecordQuery(articleIds: Set<Long>) = dslContext.select(
        ARTICLE_MAIN_CARD.ID.`as`(ArticleMainCardRecord::articleId.name),
        ARTICLE_MAIN_CARD.TITLE.`as`(ArticleMainCardRecord::articleTitle.name),
        ARTICLE_MAIN_CARD.MAIN_IMAGE_URL.`as`(ArticleMainCardRecord::mainImageUrl.name),
        ARTICLE_MAIN_CARD.CATEGORY_CD.`as`(ArticleMainCardRecord::categoryCd.name),
        ARTICLE_MAIN_CARD.CREATED_AT.`as`(ArticleMainCardRecord::createdAt.name),
        ARTICLE_MAIN_CARD.WRITER_ID.`as`(ArticleMainCardRecord::writerId.name),
        ARTICLE_MAIN_CARD.WRITER_EMAIL.`as`(ArticleMainCardRecord::writerEmail.name),
        ARTICLE_MAIN_CARD.WRITER_IMG_URL.`as`(ArticleMainCardRecord::writerImgUrl.name),
        jsonGetAttributeAsText(
            ARTICLE_MAIN_CARD.WRITER_DESCRIPTION,
            "name"
        ).`as`(ArticleMainCardRecord::writerName.name),
        jsonGetAttribute(ARTICLE_MAIN_CARD.WRITER_DESCRIPTION, "url").`as`(ArticleMainCardRecord::writerUrl.name),
        ARTICLE_MAIN_CARD.WORKBOOKS.`as`(ArticleMainCardRecord::workbooks.name)
    ).from(ARTICLE_MAIN_CARD)
        .where(ARTICLE_MAIN_CARD.ID.`in`(articleIds))
        .query

    /**
     * NOTE - The query performed in this function do not save the workbook.
     */
    fun insertArticleMainCard(command: ArticleMainCardExcludeWorkbookCommand) =
        insertArticleMainCardQuery(command).execute()

    fun insertArticleMainCardQuery(command: ArticleMainCardExcludeWorkbookCommand) = dslContext
        .insertInto(ARTICLE_MAIN_CARD)
        .set(ARTICLE_MAIN_CARD.ID, command.articleId)
        .set(ARTICLE_MAIN_CARD.TITLE, command.articleTitle)
        .set(ARTICLE_MAIN_CARD.MAIN_IMAGE_URL, command.mainImageUrl.toString())
        .set(ARTICLE_MAIN_CARD.CATEGORY_CD, command.categoryCd)
        .set(ARTICLE_MAIN_CARD.CREATED_AT, command.createdAt)
        .set(ARTICLE_MAIN_CARD.WRITER_ID, command.writerId)
        .set(ARTICLE_MAIN_CARD.WRITER_EMAIL, command.writerEmail)
        .set(ARTICLE_MAIN_CARD.WRITER_IMG_URL, command.writerImgUrl.toString())
        .set(
            ARTICLE_MAIN_CARD.WRITER_DESCRIPTION,
            JSON.valueOf(
                commonJsonMapper.toJsonStr(
                    mapOf(
                        "name" to command.writerName,
                        "url" to command.writerUrl
                    )
                )
            )
        )

    fun updateArticleMainCardSetWorkbook(command: UpdateArticleMainCardWorkbookCommand) =
        updateArticleMainCardSetWorkbookQuery(command).execute()

    fun updateArticleMainCardSetWorkbookQuery(command: UpdateArticleMainCardWorkbookCommand) = dslContext
        .update(ARTICLE_MAIN_CARD)
        .set(ARTICLE_MAIN_CARD.WORKBOOKS, JSON.valueOf(articleMainCardMapper.toJsonStr(command.workbooks)))
        .where(ARTICLE_MAIN_CARD.ID.eq(command.articleId))
}