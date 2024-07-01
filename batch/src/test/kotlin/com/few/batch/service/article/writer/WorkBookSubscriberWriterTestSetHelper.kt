package com.few.batch.service.article.writer

import com.few.batch.service.article.dto.WorkBookSubscriberItem
import jooq.jooq_dsl.tables.ArticleIfo
import jooq.jooq_dsl.tables.MappingWorkbookArticle
import jooq.jooq_dsl.tables.Member
import jooq.jooq_dsl.tables.Subscription
import org.jooq.DSLContext
import org.springframework.boot.test.context.TestComponent
import kotlin.random.Random

fun List<TestWorkBookSubscriberDto>.toServiceDto(): List<WorkBookSubscriberItem> {
    return this.map {
        WorkBookSubscriberItem(it.memberId, it.targetWorkBookId, it.progress)
    }
}

fun List<TestWorkBookSubscriberDto>.toMemberIds(): List<Long> {
    return this.map { it.memberId }
}

data class TestWorkBookSubscriberDto(
    val memberId: Long,
    val targetWorkBookId: Long,
    val progress: Long,
    val content: String
)

@TestComponent
class WorkBookSubscriberWriterTestSetHelper(
    private val dslContext: DSLContext
) {

    fun setUpMembers(count: Int) {
        for (i in 1..count) {
            dslContext.insertInto(Member.MEMBER)
                .set(Member.MEMBER.ID, i.toLong())
                .set(Member.MEMBER.EMAIL, "member$i@gmail.com")
                .set(Member.MEMBER.TYPE_CD, 0) // todo fix
                .execute()
        }
    }

    fun setUpSubscriptions(start: Int = 1, count: Int, workbookId: Long = 1) {
        for (i in start..count) {
            dslContext.insertInto(Subscription.SUBSCRIPTION)
                .set(Subscription.SUBSCRIPTION.MEMBER_ID, i.toLong())
                .set(Subscription.SUBSCRIPTION.TARGET_WORKBOOK_ID, workbookId)
                .set(Subscription.SUBSCRIPTION.PROGRESS, Random.nextLong(1, count.toLong() - start.toLong()))
                .execute()
        }
    }

    fun setUpMappingWorkbookArticle(start: Int = 1, count: Int, workbookId: Long = 1, dayCorrection: Int = 0) {
        for (i in start..count) {
            dslContext.insertInto(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE)
                .set(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID, workbookId)
                .set(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.ARTICLE_ID, i.toLong())
                .set(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.DAY_COL, i - dayCorrection)
                .execute()
        }
    }

    fun setUpArticleIfo(start: Int = 1, count: Int) {
        for (i in start..count) {
            dslContext.insertInto(ArticleIfo.ARTICLE_IFO)
                .set(ArticleIfo.ARTICLE_IFO.ARTICLE_MST_ID, i.toLong())
                .set(ArticleIfo.ARTICLE_IFO.CONTENT, "content$i")
                .execute()
        }
    }

    fun browseItems(targetWorkBookIds: List<Long>): List<TestWorkBookSubscriberDto> {
        val items = mutableListOf<TestWorkBookSubscriberDto>()
        targetWorkBookIds.forEach {
                workbookId ->
            dslContext.select(Subscription.SUBSCRIPTION.MEMBER_ID, Subscription.SUBSCRIPTION.PROGRESS)
                .from(Subscription.SUBSCRIPTION)
                .where(Subscription.SUBSCRIPTION.TARGET_WORKBOOK_ID.eq(workbookId))
                .fetch()
                .forEach {
                    val content = dslContext.select(
                        ArticleIfo.ARTICLE_IFO.CONTENT
                    )
                        .from(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE)
                        .join(ArticleIfo.ARTICLE_IFO)
                        .on(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.ARTICLE_ID.eq(ArticleIfo.ARTICLE_IFO.ARTICLE_MST_ID))
                        .where(
                            MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID.eq(workbookId)
                        )
                        .and(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.DAY_COL.eq(it[Subscription.SUBSCRIPTION.PROGRESS].toInt() + 1))
                        .and(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.DELETED_AT.isNull)
                        .fetchOneInto(String::class.java)

                    val dto = TestWorkBookSubscriberDto(
                        memberId = it[Subscription.SUBSCRIPTION.MEMBER_ID],
                        targetWorkBookId = workbookId,
                        progress = it[Subscription.SUBSCRIPTION.PROGRESS],
                        content = content!!
                    )
                    items.add(dto)
                }
        }
        return items
    }
}