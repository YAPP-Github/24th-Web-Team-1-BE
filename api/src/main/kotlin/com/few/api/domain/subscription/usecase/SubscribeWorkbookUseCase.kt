package com.few.api.domain.subscription.usecase

import com.few.api.domain.common.exception.NotFoundException
import com.few.api.domain.common.lock.ApiLockFor
import com.few.api.domain.common.lock.ApiLockIdentifier
import com.few.api.domain.subscription.event.dto.WorkbookSubscriptionEvent
import com.few.api.domain.subscription.exception.SubscribeIllegalArgumentException
import com.few.api.domain.subscription.repo.SubscriptionDao
import com.few.api.domain.subscription.repo.command.InsertWorkbookSubscriptionCommand
import com.few.api.domain.subscription.repo.query.CountWorkbookMappedArticlesQuery
import com.few.api.domain.subscription.repo.query.SelectAllWorkbookSubscriptionStatusNotConsiderDeletedAtQuery
import com.few.api.domain.subscription.repo.query.SelectSubscriptionSendStatusQuery
import com.few.api.domain.subscription.usecase.dto.SubscribeWorkbookUseCaseIn
import com.few.api.domain.subscription.usecase.model.CancelledWorkbookSubscriptionHistory
import com.few.api.domain.subscription.usecase.model.WorkbookSubscriptionHistory
import com.few.api.domain.subscription.usecase.model.WorkbookSubscriptionStatus
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import repo.jooq.DataSourceTransactional

@Component
class SubscribeWorkbookUseCase(
    private val subscriptionDao: SubscriptionDao,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    @ApiLockFor(ApiLockIdentifier.SUBSCRIPTION_MEMBER_ID_WORKBOOK_ID)
    @DataSourceTransactional
    fun execute(useCaseIn: SubscribeWorkbookUseCaseIn) {
        val subTargetWorkbookId = useCaseIn.workbookId
        val memberId = useCaseIn.memberId

        val command =
            subscriptionDao
                .selectSubscriptionSendStatus(
                    SelectSubscriptionSendStatusQuery(
                        memberId = memberId,
                    ),
                ).takeIf {
                    it.isNotEmpty()
                }?.let {
                    /** 현재 구독 중인 정보가 있다면 해당 정보를 통해 구독 정보를 생성 */
                    InsertWorkbookSubscriptionCommand(
                        memberId = memberId,
                        workbookId = subTargetWorkbookId,
                        sendDay = it[0].sendDay,
                        sendTime = it[0].sendTime,
                    )
                } ?: run {
                /** 현재 구독 중인 정보가 없다면 기본 정보로 구독 정보를 생성 */
                InsertWorkbookSubscriptionCommand(
                    memberId = memberId,
                    workbookId = subTargetWorkbookId,
                )
            }

        val workbookSubscriptionHistory =
            subscriptionDao
                .selectTopWorkbookSubscriptionStatus(
                    SelectAllWorkbookSubscriptionStatusNotConsiderDeletedAtQuery(
                        memberId = memberId,
                        workbookId = subTargetWorkbookId,
                    ),
                )?.let {
                    WorkbookSubscriptionHistory(
                        false,
                        WorkbookSubscriptionStatus(
                            workbookId = it.workbookId,
                            isActiveSub = it.isActiveSub,
                            day = it.day,
                        ),
                    )
                } ?: WorkbookSubscriptionHistory(true)

        when {
            /** 구독한 히스토리가 없는 경우 */
            workbookSubscriptionHistory.isNew -> {
                subscriptionDao.insertWorkbookSubscription(command)
            }

            /** 이미 구독한 히스토리가 있고 구독이 취소된 경우 */
            workbookSubscriptionHistory.isCancelSub -> {
                val cancelledWorkbookSubscriptionHistory = CancelledWorkbookSubscriptionHistory(workbookSubscriptionHistory)
                val lastDay =
                    subscriptionDao.countWorkbookMappedArticles(
                        CountWorkbookMappedArticlesQuery(
                            subTargetWorkbookId,
                        ),
                    ) ?: throw NotFoundException("workbook.notfound.id")

                if (cancelledWorkbookSubscriptionHistory.isSubEnd(lastDay)) {
                    /** 이미 구독이 종료된 경우 */
                    throw SubscribeIllegalArgumentException("subscribe.state.end")
                } else {
                    /** 재구독인 경우 */
                    subscriptionDao.reSubscribeWorkbookSubscription(command)
                }
            }

            /** 구독 중인 경우 */
            else -> {
                throw SubscribeIllegalArgumentException("subscribe.state.subscribed")
            }
        }

        applicationEventPublisher.publishEvent(
            WorkbookSubscriptionEvent(
                workbookId = subTargetWorkbookId,
                memberId = memberId,
                articleDayCol = workbookSubscriptionHistory.subDay,
            ),
        )
    }
}