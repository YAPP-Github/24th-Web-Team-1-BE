package com.few.api.repo.dao.subscription

import com.few.api.repo.dao.subscription.command.InsertWorkbookSubscriptionCommand
import com.few.api.repo.dao.subscription.command.UpdateDeletedAtInWorkbookSubscriptionCommand
import com.few.api.repo.dao.subscription.query.CountWorkbookSubscriptionQuery
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

    fun updateDeletedAtInWorkbookSubscription(command: UpdateDeletedAtInWorkbookSubscriptionCommand) {
        dslContext.update(SUBSCRIPTION)
            .set(SUBSCRIPTION.DELETED_AT, LocalDateTime.now())
            .set(SUBSCRIPTION.UNSUBS_OPINION, command.opinion)
            .where(SUBSCRIPTION.MEMBER_ID.eq(command.memberId))
            .and(SUBSCRIPTION.TARGET_WORKBOOK_ID.eq(command.workbookId))
            .execute()
    }

    fun selectCountWorkbookSubscription(query: CountWorkbookSubscriptionQuery): Int {
        return dslContext.selectCount()
            .from(SUBSCRIPTION)
            .where(SUBSCRIPTION.MEMBER_ID.eq(query.memberId))
            .and(SUBSCRIPTION.TARGET_WORKBOOK_ID.eq(query.workbookId))
            .and(SUBSCRIPTION.DELETED_AT.isNull)
            .fetchOne(0, Int::class.java) ?: 0
    }
}