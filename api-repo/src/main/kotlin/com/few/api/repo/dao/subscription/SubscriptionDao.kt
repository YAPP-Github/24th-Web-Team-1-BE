package com.few.api.repo.dao.subscription

import com.few.api.repo.dao.subscription.command.InsertWorkbookSubscriptionCommand
import com.few.api.repo.dao.subscription.command.UpdateDeletedAtInAllSubscriptionCommand
import com.few.api.repo.dao.subscription.command.UpdateDeletedAtInWorkbookSubscriptionCommand
import com.few.api.repo.dao.subscription.query.SelectAllWorkbookSubscriptionStatusQueryNotConsiderDeletedAt
import com.few.api.repo.dao.subscription.record.WorkbookSubscriptionStatus
import com.few.api.repo.dao.subscription.query.CountWorkbookMappedArticlesQuery
import jooq.jooq_dsl.Tables.MAPPING_WORKBOOK_ARTICLE
import jooq.jooq_dsl.Tables.SUBSCRIPTION
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class SubscriptionDao(
    private val dslContext: DSLContext
) {

    fun insertWorkbookSubscription(command: InsertWorkbookSubscriptionCommand) {
        dslContext.insertInto(SUBSCRIPTION)
            .set(SUBSCRIPTION.MEMBER_ID, command.memberId)
            .set(SUBSCRIPTION.TARGET_WORKBOOK_ID, command.workbookId)
            .returning(SUBSCRIPTION.ID)
            .fetchOne()
    }

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

    fun selectAllWorkbookSubscriptionStatus(query: SelectAllWorkbookSubscriptionStatusQueryNotConsiderDeletedAt): List<WorkbookSubscriptionStatus> {
        return dslContext.select(
            SUBSCRIPTION.ID.`as`(WorkbookSubscriptionStatus::id.name),
            SUBSCRIPTION.DELETED_AT.isNotNull.`as`(WorkbookSubscriptionStatus::subHistory.name),
            SUBSCRIPTION.PROGRESS.add(1).`as`(WorkbookSubscriptionStatus::day.name)
        )
            .from(SUBSCRIPTION)
            .where(SUBSCRIPTION.MEMBER_ID.eq(query.memberId))
            .and(SUBSCRIPTION.TARGET_WORKBOOK_ID.eq(query.workbookId))
            .fetchInto(WorkbookSubscriptionStatus::class.java)
    }

    fun updateDeletedAtInAllSubscription(command: UpdateDeletedAtInAllSubscriptionCommand) {
        dslContext.update(SUBSCRIPTION)
            .set(SUBSCRIPTION.DELETED_AT, LocalDateTime.now())
            .set(SUBSCRIPTION.UNSUBS_OPINION, command.opinion) // TODO: opinion row 마다 중복 해결
            .where(SUBSCRIPTION.MEMBER_ID.eq(command.memberId))
            .execute()
    }

    fun countWorkbookMappedArticles(query: CountWorkbookMappedArticlesQuery): Int? {
        return dslContext.selectCount()
            .from(MAPPING_WORKBOOK_ARTICLE)
            .where(MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID.eq(query.workbookId))
            .fetchOne(0, Int::class.java)
    }
}