package com.few.api.repo.dao.subscription

import com.few.api.repo.dao.subscription.command.InsertWorkbookSubscriptionCommand
import com.few.api.repo.dao.subscription.command.UpdateDeletedAtInAllSubscriptionCommand
import com.few.api.repo.dao.subscription.command.UpdateDeletedAtInWorkbookSubscriptionCommand
import com.few.api.repo.dao.subscription.query.SelectAllWorkbookSubscriptionStatusNotConsiderDeletedAtQuery
import com.few.api.repo.dao.subscription.record.WorkbookSubscriptionStatus
import com.few.api.repo.dao.subscription.query.CountWorkbookMappedArticlesQuery
import com.few.api.repo.dao.subscription.record.CountAllSubscriptionStatusRecord
import jooq.jooq_dsl.Tables.MAPPING_WORKBOOK_ARTICLE
import jooq.jooq_dsl.Tables.SUBSCRIPTION
import org.jooq.DSLContext
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
        dslContext.update(SUBSCRIPTION)
            .set(SUBSCRIPTION.DELETED_AT, null as LocalDateTime?)
            .set(SUBSCRIPTION.UNSUBS_OPINION, null as String?)
            .where(SUBSCRIPTION.MEMBER_ID.eq(command.memberId))
            .and(SUBSCRIPTION.TARGET_WORKBOOK_ID.eq(command.workbookId))
            .execute()
    }

    fun updateDeletedAtInWorkbookSubscription(command: UpdateDeletedAtInWorkbookSubscriptionCommand) {
        dslContext.update(SUBSCRIPTION)
            .set(SUBSCRIPTION.DELETED_AT, LocalDateTime.now())
            .set(SUBSCRIPTION.UNSUBS_OPINION, command.opinion)
            .where(SUBSCRIPTION.MEMBER_ID.eq(command.memberId))
            .and(SUBSCRIPTION.TARGET_WORKBOOK_ID.eq(command.workbookId))
            .execute()
    }

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
}