package com.few.batch.service.article.writer

import com.few.batch.service.article.dto.WorkBookSubscriberItem
import com.few.batch.service.article.dto.toMemberIds
import com.few.batch.service.article.dto.toTargetWorkBookIds
import com.few.batch.service.article.dto.toTargetWorkBookProgress
import com.few.batch.service.article.writer.model.*
import com.few.batch.service.article.writer.service.*
import com.few.batch.service.article.writer.support.MailSendRecorder
import com.few.batch.service.article.writer.support.MailServiceArgsGenerator
import com.few.email.service.article.SendArticleEmailService
import jooq.jooq_dsl.tables.*
import org.jooq.DSLContext
import org.jooq.UpdateConditionStep
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class WorkBookSubscriberWriter(
    private val dslContext: DSLContext,

    private val browseMemberEmailService: BrowseMemberEmailService,
    private val browseMemberReceiveArticlesService: BrowseMemberReceiveArticlesService,
    private val browseArticleContentsService: BrowseArticleContentsService,
    private val browseWorkbookLastDayColService: BrowseWorkbookLastDayColService,

    private val sendArticleEmailService: SendArticleEmailService,
) {

    /**
     * 구독자들에게 이메일을 전송하고 진행률을 업데이트한다.
     */
    @Transactional
    fun execute(items: List<WorkBookSubscriberItem>): Map<Any, Any> {
        val memberIds = items.toMemberIds()
        val targetWorkBookIds = items.toTargetWorkBookIds()
        val targetWorkBookProgress = items.toTargetWorkBookProgress()

        /** 이메일 전송을 위한 데이터 조회 */
        val memberEmailRecords = browseMemberEmailService.execute(memberIds)
        val workbooksMappedLastDayCol = browseWorkbookLastDayColService.execute(targetWorkBookIds)
        val memberReceiveArticles =
            browseMemberReceiveArticlesService.execute(targetWorkBookProgress)
        val articleContents = browseArticleContentsService.execute(memberReceiveArticles.getArticleIds())

        /** 조회한 데이터를 이용하여 이메일 전송을 위한 인자 생성 */
        val emailServiceArgs = MailServiceArgsGenerator(
            LocalDate.now(),
            items,
            memberEmailRecords,
            memberReceiveArticles,
            articleContents
        ).generate()

        /** 이메일 전송 */
        val mailSendRecorder = MailSendRecorder(emailServiceArgs)
        emailServiceArgs.forEach {
            try {
                sendArticleEmailService.send(it.sendArticleEmailArgs)
            } catch (e: Exception) {
                mailSendRecorder.recordFail(
                    it.memberId,
                    it.workbookId,
                    e.message ?: "Unknown Error"
                )
            }
        }

        /** 이메일 전송 결과에 따라 진행률 업데이트 및 구독 해지 처리를 위한 데이터 생성 */
        val receiveLastDayRecords =
            ReceiveLastArticleRecordFilter(items, workbooksMappedLastDayCol).filter()
                .map {
                    ReceiveLastArticleRecord(it.memberId, it.targetWorkBookId)
                }

        val updateTargetMemberRecords = UpdateProgressRecordFilter(
            items,
            mailSendRecorder.getSuccessMemberIds(),
            receiveLastDayRecords.getMemberIds()
        ).filter().map {
            UpdateProgressRecord(it.memberId, it.targetWorkBookId, it.progress)
        }

        /** 진행률 업데이트 */
        val updateQueries = mutableListOf<UpdateConditionStep<*>>()
        for (updateTargetMemberRecord in updateTargetMemberRecords) {
            updateQueries.add(
                dslContext.update(Subscription.SUBSCRIPTION)
                    .set(Subscription.SUBSCRIPTION.PROGRESS, updateTargetMemberRecord.updatedProgress)
                    .where(Subscription.SUBSCRIPTION.MEMBER_ID.eq(updateTargetMemberRecord.memberId))
                    .and(Subscription.SUBSCRIPTION.TARGET_WORKBOOK_ID.eq(updateTargetMemberRecord.targetWorkBookId))
            )
        }
        dslContext.batch(updateQueries).execute()

        /** 학습지의 마지막 아티클을 받은 구독자 구독 해지 */
        val receiveLastDayQueries = mutableListOf<UpdateConditionStep<*>>()
        for (receiveLastDayMember in receiveLastDayRecords) {
            receiveLastDayQueries.add(
                dslContext.update(Subscription.SUBSCRIPTION)
                    .set(Subscription.SUBSCRIPTION.DELETED_AT, LocalDateTime.now())
                    .set(Subscription.SUBSCRIPTION.UNSUBS_OPINION, "receive.all")
                    .where(Subscription.SUBSCRIPTION.MEMBER_ID.eq(receiveLastDayMember.memberId))
                    .and(Subscription.SUBSCRIPTION.TARGET_WORKBOOK_ID.eq(receiveLastDayMember.targetWorkBookId))
            )
        }
        dslContext.batch(receiveLastDayQueries).execute()

        return mailSendRecorder.getExecutionResult()
    }
}