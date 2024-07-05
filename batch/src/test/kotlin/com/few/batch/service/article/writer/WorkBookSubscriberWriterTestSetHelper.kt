package com.few.batch.service.article.writer

import com.few.batch.data.common.code.BatchCategoryType
import com.few.batch.data.common.code.BatchMemberType
import com.few.batch.service.article.dto.WorkBookSubscriberItem
import com.few.email.service.article.dto.Content
import jooq.jooq_dsl.tables.*
import org.jooq.DSLContext
import org.jooq.JSON
import org.springframework.boot.test.context.TestComponent
import java.net.URL
import java.time.LocalDate
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
    val content: Content
)

data class ArticleDto(
    val articleId: Long,
    val dayCol: Int,
    val content: String,
    val title: String
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
                .set(Member.MEMBER.TYPE_CD, BatchMemberType.NORMAL.code)
                .execute()
        }
    }

    fun setUpWriter(id: Long) {
        dslContext.insertInto(Member.MEMBER)
            .set(Member.MEMBER.ID, id)
            .set(Member.MEMBER.EMAIL, "writer@gmail.com")
            .set(Member.MEMBER.TYPE_CD, BatchMemberType.WRITER.code)
            .set(Member.MEMBER.DESCRIPTION, JSON.valueOf("{\"url\": \"http://localhost:8080\", \"name\": \"writer\"}"))
            .execute()
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

    fun setUpMappingWorkbookArticle(start: Int = 1, count: Int, workbookId: Long = 1, dayCorrection: Int = 1) {
        for (i in start..count) {
            dslContext.insertInto(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE)
                .set(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID, workbookId)
                .set(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.ARTICLE_ID, i.toLong())
                .set(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.DAY_COL, i - dayCorrection)
                .execute()
        }
    }

    fun setUpArticleMst(start: Int = 1, count: Int, writerId: Long) {
        for (i in start..count) {
            dslContext.insertInto(ArticleMst.ARTICLE_MST)
                .set(ArticleMst.ARTICLE_MST.ID, i.toLong())
                .set(ArticleMst.ARTICLE_MST.TITLE, "title$i")
                .set(ArticleMst.ARTICLE_MST.MEMBER_ID, writerId)
                .set(ArticleMst.ARTICLE_MST.CATEGORY_CD, BatchCategoryType.fromCode(0)!!.code)
                .set(ArticleMst.ARTICLE_MST.MAIN_IMAGE_URL, "http://localhost:8080")
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

                    val articleDto = dslContext.select(
                        MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.ARTICLE_ID,
                        MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.DAY_COL,
                        ArticleIfo.ARTICLE_IFO.CONTENT,
                        ArticleMst.ARTICLE_MST.TITLE
                    ).from(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE)
                        .join(ArticleIfo.ARTICLE_IFO)
                        .on(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.ARTICLE_ID.eq(ArticleIfo.ARTICLE_IFO.ARTICLE_MST_ID))
                        .join(ArticleMst.ARTICLE_MST)
                        .on(ArticleIfo.ARTICLE_IFO.ARTICLE_MST_ID.eq(ArticleMst.ARTICLE_MST.ID))
                        .where(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID.eq(workbookId))
                        .and(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.DAY_COL.eq(it[Subscription.SUBSCRIPTION.PROGRESS].toInt() + 1))
                        .fetchOneInto(ArticleDto::class.java)

                    val dto = TestWorkBookSubscriberDto(
                        memberId = it[Subscription.SUBSCRIPTION.MEMBER_ID],
                        targetWorkBookId = workbookId,
                        progress = it[Subscription.SUBSCRIPTION.PROGRESS],
                        content = Content(
                            articleLink = URL("https://www.fewletter.com/article/${articleDto!!.articleId}"),
                            currentDate = LocalDate.now(),
                            category = BatchCategoryType.fromCode(0)!!.displayName,
                            articleDay = articleDto.dayCol,
                            articleTitle = articleDto.title,
                            writerName = "writer",
                            writerLink = URL("http://localhost:8080"),
                            articleContent = articleDto.content,
                            problemLink = URL("https://www.fewletter.com/problem?articleId=${articleDto.articleId}"),
                            unsubscribeLink = URL("https://www.fewletter.com/unsbuscribe?user=member${it[Subscription.SUBSCRIPTION.MEMBER_ID]}@gmail.com&workbookId=$workbookId&articleId=${articleDto.articleId}")
                        )
                    )
                    items.add(dto)
                }
        }
        return items
    }
}