package com.few.batch.service.article.writer

import com.few.batch.data.common.code.BatchCategoryType
import com.few.batch.data.common.code.BatchMemberType
import com.few.batch.service.article.dto.WorkBookSubscriberItem
import com.few.batch.service.article.dto.toMemberIds
import com.few.batch.service.article.dto.toTargetWorkBookIds
import com.few.batch.service.article.dto.toTargetWorkBookProgress
import com.few.email.service.article.SendArticleEmailService
import com.few.email.service.article.dto.Content
import com.few.email.service.article.dto.SendArticleEmailArgs
import jooq.jooq_dsl.tables.*
import org.jooq.DSLContext
import org.jooq.UpdateConditionStep
import org.jooq.impl.DSL
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime

data class MemberReceiveArticle(
    val workbookId: Long,
    val articleId: Long,
    val dayCol: Long,
)

data class MemberReceiveArticles(
    val articles: List<MemberReceiveArticle>,
) {
    fun getByWorkBookIdAndDayCol(workbookId: Long, dayCol: Long): MemberReceiveArticle {
        return articles.find {
            it.workbookId == workbookId && it.dayCol == dayCol
        } ?: throw IllegalArgumentException("Cannot find article by workbookId: $workbookId, dayCol: $dayCol")
    }

    fun getArticleIds(): List<Long> {
        return articles.map {
            it.articleId
        }
    }
}

data class ArticleContent(
    val id: Long,
    val category: String, // todo fix
    val articleTitle: String,
    val articleContent: String,
    val writerName: String,
    val writerLink: URL,
)

data class ReceiveLastDayMember(
    val memberId: Long,
    val targetWorkBookId: Long,
)

fun List<ArticleContent>.peek(articleId: Long): ArticleContent {
    return this.find {
        it.id == articleId
    } ?: throw IllegalArgumentException("Cannot find article by articleId: $articleId")
}

@Component
class WorkBookSubscriberWriter(
    private val dslContext: DSLContext,
    private val sendArticleEmailService: SendArticleEmailService,
) {

    companion object {
        private const val ARTICLE_SUBJECT_TEMPLATE = "Day%d %s"
        private const val ARTICLE_TEMPLATE = "article"
    }

    @Transactional
    fun execute(items: List<WorkBookSubscriberItem>): Map<Any, Any> {
        val memberT = Member.MEMBER
        val mappingWorkbookArticleT = MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE
        val articleIfoT = ArticleIfo.ARTICLE_IFO
        val articleMstT = ArticleMst.ARTICLE_MST
        val subscriptionT = Subscription.SUBSCRIPTION

        val memberIds = items.toMemberIds()
        val targetWorkBookIds = items.toTargetWorkBookIds()
        val targetWorkBookProgress = items.toTargetWorkBookProgress()

        /** 회원 ID를 기준으로 이메일을 조회한다.*/
        val memberEmailRecords = dslContext.select(
            memberT.ID,
            memberT.EMAIL
        )
            .from(memberT)
            .where(memberT.ID.`in`(memberIds))
            .fetch()
            .intoMap(memberT.ID, memberT.EMAIL)

        /** 구독자들이 구독한 학습지 ID와 구독자의 학습지 구독 진행률을 기준으로 구독자가 받을 학습지 정보를 조회한다.*/
        val memberReceiveArticles = targetWorkBookProgress.keys.stream().map { workbookId ->
            val dayCols = targetWorkBookProgress[workbookId]!!.stream().map { it + 1L }.toList()
            // todo check refactoring
            dslContext.select(
                mappingWorkbookArticleT.WORKBOOK_ID.`as`(MemberReceiveArticle::workbookId.name),
                mappingWorkbookArticleT.ARTICLE_ID.`as`(MemberReceiveArticle::articleId.name),
                mappingWorkbookArticleT.DAY_COL.`as`(MemberReceiveArticle::dayCol.name)
            )
                .from(mappingWorkbookArticleT)
                .where(
                    mappingWorkbookArticleT.WORKBOOK_ID.eq(workbookId)
                )
                .and(mappingWorkbookArticleT.DAY_COL.`in`(dayCols))
                .and(mappingWorkbookArticleT.DELETED_AT.isNull)
                .fetchInto(MemberReceiveArticle::class.java)
        }.flatMap { it.stream() }.toList().let {
            MemberReceiveArticles(it)
        }

        /** 구독자들이 받을 학습지 정보를 기준으로 학습지 관련 정보를 조회한다.*/
        val articleContents = dslContext.select(
            articleIfoT.ARTICLE_MST_ID.`as`(ArticleContent::id.name),
            articleIfoT.CONTENT.`as`(ArticleContent::articleContent.name),
            articleMstT.TITLE.`as`(ArticleContent::articleTitle.name),
            articleMstT.CATEGORY_CD.`as`(ArticleContent::category.name),
            DSL.jsonGetAttributeAsText(memberT.DESCRIPTION, "name").`as`(ArticleContent::writerName.name),
            DSL.jsonGetAttribute(memberT.DESCRIPTION, "url").`as`(ArticleContent::writerLink.name)
        )
            .from(articleIfoT)
            .join(articleMstT)
            .on(articleIfoT.ARTICLE_MST_ID.eq(articleMstT.ID))
            .join(memberT)
            .on(
                articleMstT.MEMBER_ID.eq(memberT.ID)
                    .and(memberT.TYPE_CD.eq(BatchMemberType.WRITER.code))
            )
            .where(articleIfoT.ARTICLE_MST_ID.`in`(memberReceiveArticles.getArticleIds()))
            .and(articleIfoT.DELETED_AT.isNull)
            .fetchInto(ArticleContent::class.java)

        // todo fix
        val memberSuccessRecords = memberIds.associateWith { true }.toMutableMap()
        val failRecords = mutableMapOf<String, ArrayList<Map<Long, String>>>()
        // todo check !! target is not null
        val date = LocalDate.now()
        val emailServiceArgs = items.map {
            val toEmail = memberEmailRecords[it.memberId]!!
            val memberArticle = memberReceiveArticles.getByWorkBookIdAndDayCol(it.targetWorkBookId, it.progress + 1)
            val articleContent = articleContents.peek(memberArticle.articleId).let { article ->
                Content(
                    articleLink = URL("https://www.fewletter.com/article/${memberArticle.articleId}"),
                    currentDate = date,
                    category = BatchCategoryType.convertToDisplayName(article.category.toByte()),
                    articleDay = memberArticle.dayCol.toInt(),
                    articleTitle = article.articleTitle,
                    writerName = article.writerName,
                    writerLink = article.writerLink,
                    articleContent = article.articleContent,
                    problemLink = URL("https://www.fewletter.com/problem?articleId=${memberArticle.articleId}"),
                    unsubscribeLink = URL("https://www.fewletter.com/unsubscribe?user=${memberEmailRecords[it.memberId]}&workbookId=${it.targetWorkBookId}&articleId=${memberArticle.articleId}")
                )
            }
            return@map it.memberId to
                SendArticleEmailArgs(
                    toEmail,
                    ARTICLE_SUBJECT_TEMPLATE.format(memberArticle.dayCol, articleContent.articleTitle),
                    ARTICLE_TEMPLATE,
                    articleContent
                )
        }

        // todo refactoring to send email in parallel or batch
        emailServiceArgs.forEach {
            try {
                sendArticleEmailService.send(it.second)
            } catch (e: Exception) {
                memberSuccessRecords[it.first] = false
                failRecords["EmailSendFail"] = failRecords.getOrDefault("EmailSendFail", arrayListOf()).apply {
                    val message = e.message ?: "Unknown Error"
                    add(mapOf(it.first to message))
                }
            }
        }

        /** 워크북 마지막 학습지 DAY_COL을 조회한다 */
        val lastDayCol = dslContext.select(
            mappingWorkbookArticleT.WORKBOOK_ID,
            DSL.max(mappingWorkbookArticleT.DAY_COL)
        )
            .from(mappingWorkbookArticleT)
            .where(mappingWorkbookArticleT.WORKBOOK_ID.`in`(targetWorkBookIds))
            .and(mappingWorkbookArticleT.DELETED_AT.isNull)
            .groupBy(mappingWorkbookArticleT.WORKBOOK_ID)
            .fetch()
            .intoMap(mappingWorkbookArticleT.WORKBOOK_ID, DSL.max(mappingWorkbookArticleT.DAY_COL))

        /** 마지막 학습지를 받은 구독자들의 ID를 필터링한다.*/
        val receiveLastDayMembers = items.filter {
            it.targetWorkBookId in lastDayCol.keys
        }.filter {
            (it.progress.toInt() + 1) == lastDayCol[it.targetWorkBookId]
        }.map {
            ReceiveLastDayMember(it.memberId, it.targetWorkBookId)
        }.filter {
            memberSuccessRecords[it.memberId] == true
        }

        /** 이메일 전송에 성공한 구독자들의 진행률을 업데이트한다.*/
        val successMemberIds = memberSuccessRecords.filter { it.value }.keys
        val updateTargetMemberRecords = items.filter { it.memberId in successMemberIds }
        val updateQueries = mutableListOf<UpdateConditionStep<*>>()
        for (updateTargetMemberRecord in updateTargetMemberRecords) {
            updateQueries.add(
                dslContext.update(subscriptionT)
                    .set(subscriptionT.PROGRESS, updateTargetMemberRecord.progress + 1)
                    .where(subscriptionT.MEMBER_ID.eq(updateTargetMemberRecord.memberId))
                    .and(subscriptionT.TARGET_WORKBOOK_ID.eq(updateTargetMemberRecord.targetWorkBookId))
            )
        }
        dslContext.batch(updateQueries).execute()

        /** 마지막 학습지를 받은 구독자들은 구독을 해지한다.*/
        // todo refactoring to batch update
        for (receiveLastDayMember in receiveLastDayMembers) {
            dslContext.update(subscriptionT)
                .set(subscriptionT.DELETED_AT, LocalDateTime.now())
                .set(subscriptionT.UNSUBS_OPINION, "receive.all")
                .where(subscriptionT.MEMBER_ID.eq(receiveLastDayMember.memberId))
                .and(subscriptionT.TARGET_WORKBOOK_ID.eq(receiveLastDayMember.targetWorkBookId))
                .execute()
        }

        return if (failRecords.isNotEmpty()) {
            mapOf("records" to memberSuccessRecords, "fail" to failRecords)
        } else {
            mapOf("records" to memberSuccessRecords)
        }
    }
}