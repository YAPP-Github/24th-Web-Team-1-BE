package com.few.api.domain.batch.article.reader

import com.few.api.domain.batch.article.dto.WorkBookSubscriberItem
import com.few.api.domain.common.vo.DayCode
import jooq.jooq_dsl.tables.Subscription.SUBSCRIPTION
import jooq.jooq_dsl.tables.records.SubscriptionRecord
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.TableField
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@Component
class WorkBookSubscriberReader(
    private val dslContext: DSLContext,
) {
    /** 구독 테이블에서 학습지를 구독하고 있는 회원의 정보를 조회한다.*/
    @Transactional(readOnly = true)
    fun execute(): List<WorkBookSubscriberItem> {
        val time = LocalTime.now(ZoneId.of("Asia/Seoul")).hour.let { LocalTime.of(it, 0, 0) }
        val date = LocalDate.now(ZoneId.of("Asia/Seoul"))
        val sendDay =
            if ((date.dayOfWeek == DayOfWeek.SATURDAY) || (date.dayOfWeek == DayOfWeek.SUNDAY)) {
                DayCode.MON_TUE_WED_THU_FRI_SAT_SUN
            } else {
                DayCode.MON_TUE_WED_THU_FRI
            }

        return dslContext
            .select(
                SUBSCRIPTION.MEMBER_ID.`as`(WorkBookSubscriberItem::memberId.name),
                SUBSCRIPTION.TARGET_WORKBOOK_ID.`as`(WorkBookSubscriberItem::targetWorkBookId.name),
                SUBSCRIPTION.PROGRESS.`as`(WorkBookSubscriberItem::progress.name),
            ).from(SUBSCRIPTION)
            .where(SUBSCRIPTION.SEND_TIME.eq(time))
            .and(sendDayCondition(SUBSCRIPTION.SEND_DAY, sendDay))
            .and(SUBSCRIPTION.TARGET_MEMBER_ID.isNull)
            .and(SUBSCRIPTION.DELETED_AT.isNull)
            .fetchInto(WorkBookSubscriberItem::class.java)
    }

    private fun sendDayCondition(
        sendDayField: TableField<SubscriptionRecord, String>,
        sendDayCode: DayCode,
    ): Condition =
        when (sendDayCode) {
            /** 평일인 경우 매일을 포함하여 전송한다 */
            DayCode.MON_TUE_WED_THU_FRI -> {
                sendDayField
                    .eq(DayCode.MON_TUE_WED_THU_FRI.code)
                    .or(sendDayField.eq(DayCode.MON_TUE_WED_THU_FRI_SAT_SUN.code))
            }
            /** 매일의 경우 매일만 전송한다 */
            DayCode.MON_TUE_WED_THU_FRI_SAT_SUN -> {
                sendDayField.eq(DayCode.MON_TUE_WED_THU_FRI_SAT_SUN.code)
            }
            else -> {
                throw IllegalArgumentException("Invalid sendDayCode: $sendDayCode")
            }
        }
}