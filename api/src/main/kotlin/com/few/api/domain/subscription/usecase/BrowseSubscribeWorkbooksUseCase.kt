package com.few.api.domain.subscription.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import com.few.api.domain.subscription.service.SubscriptionArticleService
import com.few.api.domain.subscription.service.dto.ReadArticleIdByWorkbookIdAndDayDto
import com.few.api.domain.subscription.usecase.dto.BrowseSubscribeWorkbooksUseCaseIn
import com.few.api.domain.subscription.usecase.dto.BrowseSubscribeWorkbooksUseCaseOut
import com.few.api.domain.subscription.usecase.dto.SubscribeWorkbookDetail
import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.query.CountAllWorkbooksSubscriptionQuery
import com.few.api.repo.dao.subscription.query.SelectAllMemberWorkbookActiveSubscriptionQuery
import com.few.api.repo.dao.subscription.query.SelectAllMemberWorkbookInActiveSubscriptionQuery
import com.few.api.web.support.WorkBookStatus
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.webjars.NotFoundException

@Component
class BrowseSubscribeWorkbooksUseCase(
    private val subscriptionDao: SubscriptionDao,
    private val subscriptionArticleService: SubscriptionArticleService,
    private val objectMapper: ObjectMapper,
) {
    @Transactional
    fun execute(useCaseIn: BrowseSubscribeWorkbooksUseCaseIn): BrowseSubscribeWorkbooksUseCaseOut {
        val inActiveSubscriptionRecords =
            subscriptionDao.selectAllInActiveWorkbookSubscriptionStatus(
                SelectAllMemberWorkbookInActiveSubscriptionQuery(useCaseIn.memberId)
            )

        val activeSubscriptionRecords = subscriptionDao.selectAllActiveWorkbookSubscriptionStatus(
            SelectAllMemberWorkbookActiveSubscriptionQuery(useCaseIn.memberId)
        )

        val subscriptionRecords = inActiveSubscriptionRecords + activeSubscriptionRecords

        /**
         * key: workbookId
         * value: workbook의 currentDay에 해당하는 articleId
         */
        val workbookSubscriptionCurrentArticleIdRecords = subscriptionRecords.associate { record ->
            val articleId = subscriptionArticleService.readArticleIdByWorkbookIdAndDay(
                ReadArticleIdByWorkbookIdAndDayDto(record.workbookId, record.currentDay)
            ) ?: throw NotFoundException("article.notfound.workbookIdAndCurrentDay")

            record.workbookId to articleId
        }

        val subscriptionWorkbookIds = subscriptionRecords.map { it.workbookId }
        val workbookSubscriptionCountRecords = subscriptionDao.countAllWorkbookSubscription(
            CountAllWorkbooksSubscriptionQuery(subscriptionWorkbookIds)
        )

        subscriptionRecords.map {
            /**
             * 임시 코드
             * Batch 코드에서 currentDay가 totalDay보다 큰 경우가 발생하여
             * currentDay가 totalDay보다 크면 totalDay로 변경
             * */
            var currentDay = it.currentDay
            if (it.currentDay > it.totalDay) {
                currentDay = it.totalDay
            }

            SubscribeWorkbookDetail(
                workbookId = it.workbookId,
                isActiveSub = WorkBookStatus.fromStatus(it.isActiveSub),
                currentDay = currentDay,
                totalDay = it.totalDay,
                totalSubscriber = workbookSubscriptionCountRecords[it.workbookId]?.toLong() ?: 0,
                articleInfo = objectMapper.writeValueAsString(
                    mapOf(
                        "articleId" to workbookSubscriptionCurrentArticleIdRecords[it.workbookId]
                    )
                )
            )
        }.let {
            return BrowseSubscribeWorkbooksUseCaseOut(it)
        }
    }
}