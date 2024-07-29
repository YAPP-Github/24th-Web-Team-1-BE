package com.few.api.repo.dao.article

import jooq.jooq_dsl.tables.ArticleMst.ARTICLE_MST
import jooq.jooq_dsl.tables.MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE
import jooq.jooq_dsl.tables.Member.MEMBER
import jooq.jooq_dsl.tables.Workbook.WORKBOOK
import com.few.api.repo.dao.article.record.ArticleMainCardRecord
import com.few.api.repo.dao.article.support.CommonJsonMapper
import jooq.jooq_dsl.tables.ArticleMainCard.ARTICLE_MAIN_CARD
import org.jooq.*
import org.jooq.impl.DSL.*
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ArticleMainCardDao(
    private val dslContext: DSLContext,
    private val commonJsonMapper: CommonJsonMapper,
) {

    fun selectArticleMainCardsRecord(articleIds: Set<Long>): Set<ArticleMainCardRecord> {
        return selectArticleMainCardsRecordQuery(articleIds)
            .fetchInto(ArticleMainCardRecord::class.java)
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
        jsonGetAttributeAsText(ARTICLE_MAIN_CARD.WRITER_DESCRIPTION, "name").`as`(ArticleMainCardRecord::writerName.name),
        jsonGetAttribute(ARTICLE_MAIN_CARD.WRITER_DESCRIPTION, "url").`as`(ArticleMainCardRecord::writerImgUrl.name)
    ).from(ARTICLE_MAIN_CARD)
        .where(ARTICLE_MAIN_CARD.ID.`in`(articleIds))
        .query

    fun selectByArticleMstAndMemberAndMappingWorkbookArticleAndWorkbook(articleIds: Set<Long>): Set<ArticleMainCardRecord> {
        return selectByArticleMstAndMemberAndMappingWorkbookArticleAndWorkbookQuery(articleIds)
            .fetchInto(ArticleMainCardRecord::class.java)
            .toSet()
    }

    private fun selectByArticleMstAndMemberAndMappingWorkbookArticleAndWorkbookQuery(articleIds: Set<Long>):
        SelectQuery<Record9<Long, String, String, Byte, LocalDateTime, Long, String, String, JSON>> {
        val a = ARTICLE_MST.`as`("a")
        val m = MEMBER.`as`("m")
        val mwa = MAPPING_WORKBOOK_ARTICLE.`as`("mwa")
        val w = WORKBOOK.`as`("w")

        return dslContext.select(
            a.ID.`as`(ArticleMainCardRecord::articleId.name),
            a.TITLE.`as`(ArticleMainCardRecord::articleTitle.name),
            a.MAIN_IMAGE_URL.`as`(ArticleMainCardRecord::mainImageUrl.name),
            a.CATEGORY_CD.`as`(ArticleMainCardRecord::categoryCd.name),
            a.CREATED_AT.`as`(ArticleMainCardRecord::createdAt.name),
            m.ID.`as`(ArticleMainCardRecord::writerId.name),
            m.EMAIL.`as`(ArticleMainCardRecord::writerEmail.name),
            jsonGetAttributeAsText(m.DESCRIPTION, "name").`as`(ArticleMainCardRecord::writerName.name),
            jsonGetAttribute(m.DESCRIPTION, "url").`as`(ArticleMainCardRecord::writerImgUrl.name)
        )
            .from(a)
            .join(m).on(a.MEMBER_ID.eq(m.ID)).and(a.DELETED_AT.isNull).and(m.DELETED_AT.isNull)
            .leftJoin(mwa).on(a.ID.eq(mwa.ARTICLE_ID)).and(mwa.DELETED_AT.isNull)
            .leftJoin(w).on(mwa.WORKBOOK_ID.eq(w.ID)).and(w.DELETED_AT.isNull)
            .where(a.ID.`in`(articleIds))
            .groupBy(a.ID)
            .query
    }

    fun insertArticleMainCardsBulk(commands: Set<ArticleMainCardRecord>) {
        dslContext.batch(
            insertArticleMainCardsBulkQuery(commands)
        ).execute()
    }

    fun insertArticleMainCardsBulkQuery(commands: Set<ArticleMainCardRecord>):
        InsertValuesStep8<jooq.jooq_dsl.tables.records.ArticleMainCardRecord, Long, String, String, Byte, LocalDateTime, Long, String, JSON> {
        val insertStep = dslContext.insertInto(
            ARTICLE_MAIN_CARD,
            ARTICLE_MAIN_CARD.ID,
            ARTICLE_MAIN_CARD.TITLE,
            ARTICLE_MAIN_CARD.MAIN_IMAGE_URL,
            ARTICLE_MAIN_CARD.CATEGORY_CD,
            ARTICLE_MAIN_CARD.CREATED_AT,
            ARTICLE_MAIN_CARD.WRITER_ID,
            ARTICLE_MAIN_CARD.WRITER_EMAIL,
            ARTICLE_MAIN_CARD.WRITER_DESCRIPTION
        )

        for (command in commands) {
            insertStep.values(
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
        }

        return insertStep
    }
}