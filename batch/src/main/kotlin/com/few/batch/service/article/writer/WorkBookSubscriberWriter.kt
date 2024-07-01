package com.few.batch.service.article.writer

import com.few.batch.service.article.dto.WorkBookSubscriberItem
import com.few.batch.service.article.dto.toMemberIds
import com.few.batch.service.article.dto.toTargetWorkBookIds
import com.few.batch.service.article.dto.toTargetWorkBookProgress
import com.few.email.service.article.SendArticleEmailService
import com.few.email.service.article.dto.SendArticleEmailArgs
import jooq.jooq_dsl.tables.ArticleIfo
import jooq.jooq_dsl.tables.MappingWorkbookArticle
import jooq.jooq_dsl.tables.Member
import jooq.jooq_dsl.tables.Subscription
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

data class MemberReceiveArticle(
    val workbookId: Long,
    val articleId: Long,
    val dayCol: Long
)

data class MemberReceiveArticles(
    val articles: List<MemberReceiveArticle>
) {
    fun getByWorkBookIdAndDayCol(workbookId: Long, dayCol: Long): MemberReceiveArticle {
        return articles.find {
            it.workbookId == workbookId && it.dayCol == dayCol
        } ?: throw IllegalArgumentException("article not found")
    }

    fun getArticleIds(): List<Long> {
        return articles.map {
            it.articleId
        }
    }
}

@Component
class WorkBookSubscriberWriter(
    private val dslContext: DSLContext,
    private val sendArticleEmailService: SendArticleEmailService
) {

    @Transactional
    fun execute(items: List<WorkBookSubscriberItem>): Map<Any, Any> {
        val memberT = Member.MEMBER
        val mappingWorkbookArticleT = MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE
        val articleIfoT = ArticleIfo.ARTICLE_IFO
        val subscriptionT = Subscription.SUBSCRIPTION

        val ARTICLE_SUBJECT = "${LocalDate.now()} 일자 학습 아티클"
        val ARTICLE_TEMPLATE = "article" // todo fix
        val ARTICLE_STYLE = "style" // todo fix

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

        /** 구독자들이 받을 학습지 정보를 기준으로 학습지의 내용을 조회한다.*/
        val articleContentsMap = dslContext.select(
            articleIfoT.ARTICLE_MST_ID,
            articleIfoT.CONTENT
        )
            .from(articleIfoT)
            .where(articleIfoT.ARTICLE_MST_ID.`in`(memberReceiveArticles.getArticleIds()))
            .and(articleIfoT.DELETED_AT.isNull)
            .fetch()
            .intoMap(articleIfoT.ARTICLE_MST_ID, articleIfoT.CONTENT)

        val memberSuccessRecords = memberIds.associateWith { true }.toMutableMap()
        val failRecords = mutableMapOf<String, ArrayList<Long>>()
        // todo check !! target is not null
        val emailServiceArgs = items.map {
            val toEmail = memberEmailRecords[it.memberId]!!
            val memberArticle = memberReceiveArticles.getByWorkBookIdAndDayCol(it.targetWorkBookId, it.progress + 1)
            val articleContent = articleContentsMap[memberArticle.articleId]!!
            return@map it.memberId to SendArticleEmailArgs(toEmail, ARTICLE_SUBJECT, ARTICLE_TEMPLATE, articleContent, ARTICLE_STYLE)
        }

        // todo refactoring to send email in parallel or batch
        emailServiceArgs.forEach {
            try {
                sendArticleEmailService.send(it.second)
            } catch (e: Exception) {
                memberSuccessRecords[it.first] = false
                failRecords["EmailSendFail"] = failRecords.getOrDefault("EmailSendFail", arrayListOf()).apply {
                    add(it.first)
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
            it.memberId
        }.filter {
            memberSuccessRecords[it] == true
        }

        val successMemberIds = memberSuccessRecords.filter { it.value }.keys
        /** 이메일 전송에 성공한 구독자들의 진행률을 업데이트한다.*/
        dslContext.update(subscriptionT)
            .set(subscriptionT.PROGRESS, subscriptionT.PROGRESS.add(1))
            .where(subscriptionT.MEMBER_ID.`in`(successMemberIds))
            .and(subscriptionT.TARGET_WORKBOOK_ID.`in`(targetWorkBookIds))
            .execute()

        /** 마지막 학습지를 받은 구독자들은 구독을 해지한다.*/
        dslContext.update(subscriptionT)
            .set(subscriptionT.DELETED_AT, LocalDateTime.now())
            .where(subscriptionT.MEMBER_ID.`in`(receiveLastDayMembers))
            .and(subscriptionT.TARGET_WORKBOOK_ID.`in`(targetWorkBookIds))
            .execute()

        return if (failRecords.isNotEmpty()) {
            mapOf("success" to memberSuccessRecords, "fail" to failRecords)
        } else {
            mapOf("success" to memberSuccessRecords)
        }
    }
}