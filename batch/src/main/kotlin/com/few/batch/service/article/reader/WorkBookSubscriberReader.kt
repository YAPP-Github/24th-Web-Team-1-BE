package com.few.batch.service.article.reader

import com.few.batch.service.article.dto.WorkBookSubscriberItem
import jooq.jooq_dsl.tables.Subscription
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class WorkBookSubscriberReader(
    private val dslContext: DSLContext
) {

    /** 구독 테이블에서 학습지를 구독하고 있는 회원의 정보를 조회한다.*/
    @Transactional(readOnly = true)
    fun execute(): List<WorkBookSubscriberItem> {
        val subscriptionT = Subscription.SUBSCRIPTION

        return dslContext.select(
            subscriptionT.MEMBER_ID.`as`(WorkBookSubscriberItem::memberId.name),
            subscriptionT.TARGET_WORKBOOK_ID.`as`(WorkBookSubscriberItem::targetWorkBookId.name),
            subscriptionT.PROGRESS.`as`(WorkBookSubscriberItem::progress.name)
        )
            .from(subscriptionT)
            .where(subscriptionT.TARGET_MEMBER_ID.isNull)
            .and(subscriptionT.DELETED_AT.isNull)
            .fetchInto(WorkBookSubscriberItem::class.java)
    }
}