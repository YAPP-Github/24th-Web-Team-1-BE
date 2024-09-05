package com.few.api.repo.dao.subscription

import com.few.api.repo.dao.subscription.command.*
import com.few.api.repo.dao.subscription.query.*
import com.few.api.repo.dao.subscription.record.WorkbookSubscriptionStatus
import com.few.api.repo.dao.subscription.record.CountAllSubscriptionStatusRecord
import com.few.api.repo.dao.subscription.record.MemberWorkbookSubscriptionStatusRecord
import com.few.api.repo.dao.subscription.record.SubscriptionSendStatusRecord
import jooq.jooq_dsl.Tables.MAPPING_WORKBOOK_ARTICLE
import jooq.jooq_dsl.Tables.SUBSCRIPTION
import jooq.jooq_dsl.tables.MappingWorkbookArticle
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class SubscriptionDao(
    private val dslContext: DSLContext,
) {

    fun insertWorkbookSubscription(command: InsertWorkbookSubscriptionCommand) {
        insertWorkbookSubscriptionCommand(command)
            .returning(SUBSCRIPTION.ID)
            .fetchOne()
    }

    fun insertWorkbookSubscriptionCommand(command: InsertWorkbookSubscriptionCommand) =
        dslContext.insertInto(SUBSCRIPTION)
            .set(SUBSCRIPTION.MEMBER_ID, command.memberId)
            .set(SUBSCRIPTION.TARGET_WORKBOOK_ID, command.workbookId)

    fun reSubscribeWorkbookSubscription(command: InsertWorkbookSubscriptionCommand) {
        reSubscribeWorkBookSubscriptionCommand(command)
            .execute()
    }

    fun reSubscribeWorkBookSubscriptionCommand(command: InsertWorkbookSubscriptionCommand) =
        dslContext.update(SUBSCRIPTION)
            .set(SUBSCRIPTION.DELETED_AT, null as LocalDateTime?)
            .set(SUBSCRIPTION.UNSUBS_OPINION, null as String?)
            .where(SUBSCRIPTION.MEMBER_ID.eq(command.memberId))
            .and(SUBSCRIPTION.TARGET_WORKBOOK_ID.eq(command.workbookId))

    fun updateDeletedAtInWorkbookSubscription(command: UpdateDeletedAtInWorkbookSubscriptionCommand) {
        updateDeletedAtInWorkbookSubscriptionCommand(command)
            .execute()
    }

    fun updateDeletedAtInWorkbookSubscriptionCommand(command: UpdateDeletedAtInWorkbookSubscriptionCommand) =
        dslContext.update(SUBSCRIPTION)
            .set(SUBSCRIPTION.DELETED_AT, LocalDateTime.now())
            .set(SUBSCRIPTION.UNSUBS_OPINION, command.opinion)
            .where(SUBSCRIPTION.MEMBER_ID.eq(command.memberId))
            .and(SUBSCRIPTION.TARGET_WORKBOOK_ID.eq(command.workbookId))

    fun selectTopWorkbookSubscriptionStatus(query: SelectAllWorkbookSubscriptionStatusNotConsiderDeletedAtQuery): WorkbookSubscriptionStatus? {
        return selectTopWorkbookSubscriptionStatusQuery(query)
            .fetchOneInto(WorkbookSubscriptionStatus::class.java)
    }

    fun selectTopWorkbookSubscriptionStatusQuery(query: SelectAllWorkbookSubscriptionStatusNotConsiderDeletedAtQuery) =
        dslContext.select(
            SUBSCRIPTION.TARGET_WORKBOOK_ID.`as`(WorkbookSubscriptionStatus::workbookId.name),
            SUBSCRIPTION.DELETED_AT.isNull.`as`(WorkbookSubscriptionStatus::isActiveSub.name),
            SUBSCRIPTION.PROGRESS.add(1).`as`(WorkbookSubscriptionStatus::day.name)
        )
            .from(SUBSCRIPTION)
            .where(SUBSCRIPTION.MEMBER_ID.eq(query.memberId))
            .and(SUBSCRIPTION.TARGET_WORKBOOK_ID.eq(query.workbookId))
            .orderBy(SUBSCRIPTION.CREATED_AT.desc())
            .limit(1)

    fun selectAllInActiveWorkbookSubscriptionStatus(query: SelectAllMemberWorkbookInActiveSubscriptionQuery): List<MemberWorkbookSubscriptionStatusRecord> {
        return selectAllWorkbookInActiveSubscriptionStatusQuery(query)
            .fetchInto(MemberWorkbookSubscriptionStatusRecord::class.java)
    }

    fun selectAllWorkbookInActiveSubscriptionStatusQuery(query: SelectAllMemberWorkbookInActiveSubscriptionQuery) =
        dslContext.select(
            SUBSCRIPTION.TARGET_WORKBOOK_ID.`as`(MemberWorkbookSubscriptionStatusRecord::workbookId.name),
            SUBSCRIPTION.DELETED_AT.isNull.`as`(MemberWorkbookSubscriptionStatusRecord::isActiveSub.name),
            DSL.max(SUBSCRIPTION.PROGRESS).add(1).`as`(MemberWorkbookSubscriptionStatusRecord::currentDay.name),
            DSL.max(MAPPING_WORKBOOK_ARTICLE.DAY_COL).`as`(MemberWorkbookSubscriptionStatusRecord::totalDay.name)
        )
            .from(SUBSCRIPTION)
            .join(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE)
            .on(SUBSCRIPTION.TARGET_WORKBOOK_ID.eq(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID))
            .where(SUBSCRIPTION.MEMBER_ID.eq(query.memberId))
            .and(SUBSCRIPTION.TARGET_MEMBER_ID.isNull)
            .and(SUBSCRIPTION.UNSUBS_OPINION.eq(query.unsubOpinion))
            .groupBy(SUBSCRIPTION.TARGET_WORKBOOK_ID, SUBSCRIPTION.DELETED_AT)
            .query

    fun selectAllActiveWorkbookSubscriptionStatus(query: SelectAllMemberWorkbookActiveSubscriptionQuery): List<MemberWorkbookSubscriptionStatusRecord> {
        return selectAllWorkbookActiveSubscriptionStatusQuery(query)
            .fetchInto(MemberWorkbookSubscriptionStatusRecord::class.java)
    }

    fun selectAllWorkbookActiveSubscriptionStatusQuery(query: SelectAllMemberWorkbookActiveSubscriptionQuery) =
        dslContext.select(
            SUBSCRIPTION.TARGET_WORKBOOK_ID.`as`(MemberWorkbookSubscriptionStatusRecord::workbookId.name),
            SUBSCRIPTION.DELETED_AT.isNull.`as`(MemberWorkbookSubscriptionStatusRecord::isActiveSub.name),
            DSL.max(SUBSCRIPTION.PROGRESS).add(1).`as`(MemberWorkbookSubscriptionStatusRecord::currentDay.name),
            DSL.max(MAPPING_WORKBOOK_ARTICLE.DAY_COL).`as`(MemberWorkbookSubscriptionStatusRecord::totalDay.name)
        )
            .from(SUBSCRIPTION)
            .join(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE)
            .on(SUBSCRIPTION.TARGET_WORKBOOK_ID.eq(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID))
            .where(SUBSCRIPTION.MEMBER_ID.eq(query.memberId))
            .and(SUBSCRIPTION.TARGET_MEMBER_ID.isNull)
            .and(SUBSCRIPTION.UNSUBS_OPINION.isNull)
            .groupBy(SUBSCRIPTION.TARGET_WORKBOOK_ID, SUBSCRIPTION.DELETED_AT)
            .query

    fun updateDeletedAtInAllSubscription(command: UpdateDeletedAtInAllSubscriptionCommand) {
        updateDeletedAtInAllSubscriptionCommand(command)
            .execute()
    }

    fun updateDeletedAtInAllSubscriptionCommand(command: UpdateDeletedAtInAllSubscriptionCommand) =
        dslContext.update(SUBSCRIPTION)
            .set(SUBSCRIPTION.DELETED_AT, LocalDateTime.now())
            .set(SUBSCRIPTION.UNSUBS_OPINION, command.opinion) // TODO: opinion row 마다 중복 해결
            .where(SUBSCRIPTION.MEMBER_ID.eq(command.memberId))

    fun countWorkbookMappedArticles(query: CountWorkbookMappedArticlesQuery): Int? {
        return countWorkbookMappedArticlesQuery(query)
            .fetchOne(0, Int::class.java)
    }

    fun countWorkbookMappedArticlesQuery(query: CountWorkbookMappedArticlesQuery) =
        dslContext.selectCount()
            .from(MAPPING_WORKBOOK_ARTICLE)
            .where(MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID.eq(query.workbookId))

    fun countAllSubscriptionStatus(): CountAllSubscriptionStatusRecord {
        val total = dslContext.selectCount()
            .from(SUBSCRIPTION)
            .fetchOne(0, Int::class.java)!!
        val active = dslContext.selectCount()
            .from(SUBSCRIPTION)
            .where(SUBSCRIPTION.DELETED_AT.isNull)
            .fetchOne(0, Int::class.java)!!
        return CountAllSubscriptionStatusRecord(total.toLong(), active.toLong())
    }

    /**
     * key: workbookId
     * value: workbook 구독 전체 기록 수
     */
    fun countAllWorkbookSubscription(query: CountAllWorkbooksSubscriptionQuery): Map<Long, Int> {
        return countAllWorkbookSubscriptionQuery()
            .fetch()
            .intoMap(SUBSCRIPTION.TARGET_WORKBOOK_ID, DSL.count(SUBSCRIPTION.TARGET_WORKBOOK_ID))
    }

    fun countAllWorkbookSubscriptionQuery() = dslContext.select(
        SUBSCRIPTION.TARGET_WORKBOOK_ID,
        DSL.count(SUBSCRIPTION.TARGET_WORKBOOK_ID)
    )
        .from(SUBSCRIPTION)
        .groupBy(SUBSCRIPTION.TARGET_WORKBOOK_ID)
        .query

    fun updateArticleProgress(command: UpdateArticleProgressCommand) {
        updateArticleProgressCommand(command)
            .execute()
    }

    fun updateArticleProgressCommand(
        command: UpdateArticleProgressCommand,
    ) = dslContext.update(SUBSCRIPTION)
        .set(SUBSCRIPTION.PROGRESS, SUBSCRIPTION.PROGRESS.add(1))
        .where(SUBSCRIPTION.MEMBER_ID.eq(command.memberId))
        .and(SUBSCRIPTION.TARGET_WORKBOOK_ID.eq(command.workbookId))

    fun updateLastArticleProgress(command: UpdateLastArticleProgressCommand) {
        updateLastArticleProgressCommand(command)
            .execute()
    }

    fun updateLastArticleProgressCommand(command: UpdateLastArticleProgressCommand) =
        dslContext.update(SUBSCRIPTION)
            .set(SUBSCRIPTION.DELETED_AT, LocalDateTime.now())
            .set(SUBSCRIPTION.UNSUBS_OPINION, command.opinion)
            .where(SUBSCRIPTION.MEMBER_ID.eq(command.memberId))
            .and(SUBSCRIPTION.TARGET_WORKBOOK_ID.eq(command.workbookId))

    fun selectAllSubscriptionSendStatus(query: SelectAllSubscriptionSendStatusQuery): List<SubscriptionSendStatusRecord> {
        return selectAllSubscriptionSendStatusQuery(query)
            .fetchInto(
                SubscriptionSendStatusRecord::class.java
            )
    }

    fun selectAllSubscriptionSendStatusQuery(query: SelectAllSubscriptionSendStatusQuery) =
        dslContext.select(
            SUBSCRIPTION.TARGET_WORKBOOK_ID.`as`(SubscriptionSendStatusRecord::workbookId.name),
            SUBSCRIPTION.SEND_TIME.`as`(SubscriptionSendStatusRecord::sendTime.name),
            SUBSCRIPTION.SEND_DAY.`as`(SubscriptionSendStatusRecord::sendDay.name)
        )
            .from(SUBSCRIPTION)
            .where(SUBSCRIPTION.MEMBER_ID.eq(query.memberId))
            .and(SUBSCRIPTION.TARGET_WORKBOOK_ID.`in`(query.workbookIds))
}