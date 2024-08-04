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
        jsonGetAttributeAsText(
            ARTICLE_MAIN_CARD.WRITER_DESCRIPTION,
            "name"
        ).`as`(ArticleMainCardRecord::writerName.name),
        jsonGetAttribute(ARTICLE_MAIN_CARD.WRITER_DESCRIPTION, "url").`as`(ArticleMainCardRecord::writerImgUrl.name),
        ARTICLE_MAIN_CARD.WORKBOOKS.`as`(ArticleMainCardRecord::workbooks.name)
    ).from(ARTICLE_MAIN_CARD)
        .where(ARTICLE_MAIN_CARD.ID.`in`(articleIds))
        .query

//    fun selectByArticleMstAndMemberAndMappingWorkbookArticleAndWorkbook(articleIds: Set<Long>): Set<ArticleMainCardRecord> {
//        return selectByArticleMstAndMemberAndMappingWorkbookArticleAndWorkbookQuery(articleIds)
//            .fetch(articleMainCardMapper)
//            .toSet()
//    }

//    private fun selectByArticleMstAndMemberAndMappingWorkbookArticleAndWorkbookQuery(articleIds: Set<Long>) =
//        dslContext.select(
//            ARTICLE_MST.ID.`as`(ArticleMainCardRecord::articleId.name),
//            ARTICLE_MST.TITLE.`as`(ArticleMainCardRecord::articleTitle.name),
//            ARTICLE_MST.MAIN_IMAGE_URL.`as`(ArticleMainCardRecord::mainImageUrl.name),
//            ARTICLE_MST.CATEGORY_CD.`as`(ArticleMainCardRecord::categoryCd.name),
//            ARTICLE_MST.CREATED_AT.`as`(ArticleMainCardRecord::createdAt.name),
//            MEMBER.ID.`as`(ArticleMainCardRecord::writerId.name),
//            MEMBER.EMAIL.`as`(ArticleMainCardRecord::writerEmail.name),
//            jsonGetAttributeAsText(MEMBER.DESCRIPTION, "name").`as`(ArticleMainCardRecord::writerName.name),
//            jsonGetAttribute(MEMBER.DESCRIPTION, "url").`as`(ArticleMainCardRecord::writerImgUrl.name),
//            jsonArrayAgg(
//                jsonObject(
//                    key("id").value(WORKBOOK.ID),
//                    key("title").value(WORKBOOK.TITLE)
//                )
//            ).`as`(ArticleMainCardRecord::workbooks.name)
//        )
//            .from(ARTICLE_MST)
//            .join(MEMBER).on(ARTICLE_MST.MEMBER_ID.eq(MEMBER.ID)).and(ARTICLE_MST.DELETED_AT.isNull)
//            .and(MEMBER.DELETED_AT.isNull)
//            .leftJoin(MAPPING_WORKBOOK_ARTICLE).on(ARTICLE_MST.ID.eq(MAPPING_WORKBOOK_ARTICLE.ARTICLE_ID))
//            .and(MAPPING_WORKBOOK_ARTICLE.DELETED_AT.isNull)
//            .leftJoin(WORKBOOK).on(MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID.eq(WORKBOOK.ID)).and(WORKBOOK.DELETED_AT.isNull)
//            .where(ARTICLE_MST.ID.`in`(articleIds))
//            .groupBy(ARTICLE_MST.ID)
//            .query

//    fun insertArticleMainCardsBulk(commands: Set<ArticleMainCardRecord>) {
//        dslContext.batch(
//            commands.map { command -> insertArticleMainCardsBulkQuery(command) }
//        ).execute()
//    }

//    fun insertArticleMainCardsBulkQuery(command: ArticleMainCardRecord) =
//        dslContext.insertInto(
//            ARTICLE_MAIN_CARD,
//            ARTICLE_MAIN_CARD.ID,
//            ARTICLE_MAIN_CARD.TITLE,
//            ARTICLE_MAIN_CARD.MAIN_IMAGE_URL,
//            ARTICLE_MAIN_CARD.CATEGORY_CD,
//            ARTICLE_MAIN_CARD.CREATED_AT,
//            ARTICLE_MAIN_CARD.WRITER_ID,
//            ARTICLE_MAIN_CARD.WRITER_EMAIL,
//            ARTICLE_MAIN_CARD.WRITER_DESCRIPTION,
//            ARTICLE_MAIN_CARD.WORKBOOKS
//        ).values(
//            command.articleId,
//            command.articleTitle,
//            command.mainImageUrl.toString(),
//            command.categoryCd,
//            command.createdAt,
//            command.writerId,
//            command.writerEmail,
//            JSON.valueOf(
//                commonJsonMapper.toJsonStr(
//                    mapOf(
//                        "name" to command.writerName,
//                        "url" to command.writerImgUrl
//                    )
//                )
//            ),
//            JSON.valueOf(articleMainCardMapper.toJsonStr(command.workbooks))
//        )

    /**
     * NOTE - The query performed in this function do not save the workbook.
     */
    fun insertArticleMainCard(command: ArticleMainCardExcludeWorkbookCommand) =
        insertArticleMainCardQuery(command).execute()

    fun insertArticleMainCardQuery(command: ArticleMainCardExcludeWorkbookCommand) = dslContext
        .insertInto(
            ARTICLE_MAIN_CARD,
            ARTICLE_MAIN_CARD.ID,
            ARTICLE_MAIN_CARD.TITLE,
            ARTICLE_MAIN_CARD.MAIN_IMAGE_URL,
            ARTICLE_MAIN_CARD.CATEGORY_CD,
            ARTICLE_MAIN_CARD.CREATED_AT,
            ARTICLE_MAIN_CARD.WRITER_ID,
            ARTICLE_MAIN_CARD.WRITER_EMAIL,
            ARTICLE_MAIN_CARD.WRITER_DESCRIPTION
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
                        "url" to command.writerImgUrl
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