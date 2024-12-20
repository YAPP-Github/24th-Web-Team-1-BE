package com.few.api.domain.article.repo

import com.few.api.domain.article.repo.command.ArticleMainCardExcludeWorkbookCommand
import com.few.api.domain.article.repo.command.UpdateArticleMainCardWorkbookCommand
import com.few.api.domain.article.repo.record.ArticleMainCardRecord
import com.few.api.domain.article.repo.support.ArticleMainCardMapper
import com.few.api.domain.article.repo.support.CommonJsonMapper
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
    fun selectArticleMainCardsRecord(articleIds: Set<Long>): Set<ArticleMainCardRecord> =
        selectArticleMainCardsRecordQuery(articleIds)
            .fetch(articleMainCardMapper)
            .toSet()

    fun selectArticleMainCardsRecordQuery(articleIds: Set<Long>) =
        dslContext
            .select(
                ARTICLE_MAIN_CARD.ID.`as`(ArticleMainCardRecord::articleId.name),
                ARTICLE_MAIN_CARD.TITLE.`as`(ArticleMainCardRecord::articleTitle.name),
                ARTICLE_MAIN_CARD.MAIN_IMAGE_URL.`as`(ArticleMainCardRecord::mainImageUrl.name),
                ARTICLE_MAIN_CARD.CATEGORY_CD.`as`(ArticleMainCardRecord::categoryCd.name),
                ARTICLE_MAIN_CARD.CREATED_AT.`as`(ArticleMainCardRecord::createdAt.name),
                ARTICLE_MAIN_CARD.WRITER_ID.`as`(ArticleMainCardRecord::writerId.name),
                ARTICLE_MAIN_CARD.WRITER_EMAIL.`as`(ArticleMainCardRecord::writerEmail.name),
                jsonGetAttributeAsText(
                    ARTICLE_MAIN_CARD.WRITER_DESCRIPTION,
                    "name",
                ).`as`(ArticleMainCardRecord::writerName.name),
                jsonGetAttribute(ARTICLE_MAIN_CARD.WRITER_DESCRIPTION, "url").`as`(ArticleMainCardRecord::writerUrl.name),
                jsonGetAttribute(ARTICLE_MAIN_CARD.WRITER_DESCRIPTION, "imageUrl").`as`(ArticleMainCardRecord::writerImgUrl.name),
                ARTICLE_MAIN_CARD.WORKBOOKS.`as`(ArticleMainCardRecord::workbooks.name),
            ).from(ARTICLE_MAIN_CARD)
            .where(ARTICLE_MAIN_CARD.ID.`in`(articleIds))
            .query

    /**
     * NOTE - The query performed in this function do not save the workbook.
     */
    fun insertArticleMainCard(command: ArticleMainCardExcludeWorkbookCommand) = insertArticleMainCardCommand(command).execute()

    fun insertArticleMainCardCommand(command: ArticleMainCardExcludeWorkbookCommand) =
        dslContext
            .insertInto(
                ARTICLE_MAIN_CARD,
                ARTICLE_MAIN_CARD.ID,
                ARTICLE_MAIN_CARD.TITLE,
                ARTICLE_MAIN_CARD.MAIN_IMAGE_URL,
                ARTICLE_MAIN_CARD.CATEGORY_CD,
                ARTICLE_MAIN_CARD.CREATED_AT,
                ARTICLE_MAIN_CARD.WRITER_ID,
                ARTICLE_MAIN_CARD.WRITER_EMAIL,
                ARTICLE_MAIN_CARD.WRITER_DESCRIPTION,
            ).values(
                command.articleId,
                command.articleTitle,
                command.mainImageUrl.toString(),
                command.categoryCd,
                command.createdAt,
                command.writerId,
                command.writerEmail,
                JSON.valueOf(
                    commonJsonMapper.toJsonStr(
                        mapOf(
                            "name" to command.writerName,
                            "url" to command.writerUrl,
                            "imageUrl" to command.writerImgUrl,
                        ),
                    ),
                ),
            )

    fun updateArticleMainCardSetWorkbook(command: UpdateArticleMainCardWorkbookCommand) =
        updateArticleMainCardSetWorkbookCommand(command).execute()

    fun updateArticleMainCardSetWorkbookCommand(command: UpdateArticleMainCardWorkbookCommand) =
        dslContext
            .update(ARTICLE_MAIN_CARD)
            .set(ARTICLE_MAIN_CARD.WORKBOOKS, JSON.valueOf(articleMainCardMapper.toJsonStr(command.workbooks)))
            .where(ARTICLE_MAIN_CARD.ID.eq(command.articleId))
}