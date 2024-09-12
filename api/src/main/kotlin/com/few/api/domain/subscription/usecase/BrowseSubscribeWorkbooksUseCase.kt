package com.few.api.domain.subscription.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import com.few.api.domain.subscription.service.SubscriptionArticleService
import com.few.api.domain.subscription.service.SubscriptionWorkbookService
import com.few.api.domain.subscription.service.dto.ReadAllWorkbookTitleInDto
import com.few.api.domain.subscription.service.dto.ReadArticleIdByWorkbookIdAndDayDto
import com.few.api.domain.subscription.usecase.dto.*
import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.query.CountAllWorkbooksSubscriptionQuery
import com.few.api.repo.dao.subscription.query.SelectAllMemberWorkbookActiveSubscriptionQuery
import com.few.api.repo.dao.subscription.query.SelectAllMemberWorkbookInActiveSubscriptionQuery
import com.few.api.repo.dao.subscription.query.SelectAllSubscriptionSendStatusQuery
import com.few.api.web.support.ViewCategory
import com.few.api.web.support.WorkBookStatus
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.webjars.NotFoundException
import java.lang.IllegalStateException

enum class SUBSCRIBE_WORKBOOK_STRATEGY {
    /**
     * 로그인 상태에서 메인 화면에 보여질 워크북을 정렬합니다.
     * - view의 값이 MAIN_CARD이다.
     * */
    MAIN_CARD,

    /**
     * 마이페이지에서 보여질 워크북을 정렬합니다.
     * - view의 값이 MY_PAGE이다.
     * */
    MY_PAGE,
}

data class ArticleInfo(
    // todo fix articleId to id
    val articleId: Long,
)

data class WorkbookInfo(
    val id: Long,
    val title: String,
)

// todo refactor to model
@Component
class BrowseSubscribeWorkbooksUseCase(
    private val subscriptionDao: SubscriptionDao,
    private val subscriptionArticleService: SubscriptionArticleService,
    private val subscriptionWorkbookService: SubscriptionWorkbookService,
    private val objectMapper: ObjectMapper,
) {
    @Transactional
    fun execute(useCaseIn: BrowseSubscribeWorkbooksUseCaseIn): BrowseSubscribeWorkbooksUseCaseOut {
        val strategy = when (useCaseIn.view) {
            ViewCategory.MAIN_CARD -> SUBSCRIBE_WORKBOOK_STRATEGY.MAIN_CARD
            ViewCategory.MY_PAGE -> SUBSCRIBE_WORKBOOK_STRATEGY.MY_PAGE
        }

        val workbookStatusRecords = when (strategy) {
            SUBSCRIBE_WORKBOOK_STRATEGY.MAIN_CARD -> {
                val activeSubscriptionRecords = subscriptionDao.selectAllActiveWorkbookSubscriptionStatus(
                    SelectAllMemberWorkbookActiveSubscriptionQuery(useCaseIn.memberId)
                )
                val inActiveSubscriptionRecords =
                    subscriptionDao.selectAllInActiveWorkbookSubscriptionStatus(
                        SelectAllMemberWorkbookInActiveSubscriptionQuery(useCaseIn.memberId)
                    )
                activeSubscriptionRecords + inActiveSubscriptionRecords
            }
            SUBSCRIBE_WORKBOOK_STRATEGY.MY_PAGE -> {
                subscriptionDao.selectAllActiveWorkbookSubscriptionStatus(
                    SelectAllMemberWorkbookActiveSubscriptionQuery(useCaseIn.memberId)
                )
            }
        }

        val subscriptionWorkbookIds = workbookStatusRecords.map { it.workbookId }
        val subscriptionWorkbookCountRecords = subscriptionDao.countAllWorkbookSubscription(
            CountAllWorkbooksSubscriptionQuery(subscriptionWorkbookIds)
        )
        val subscriptionWorkbookSendStatusRecords = subscriptionDao.selectAllSubscriptionSendStatus(
            SelectAllSubscriptionSendStatusQuery(useCaseIn.memberId, subscriptionWorkbookIds)
        ).associateBy { it.workbookId }

        val workbookDetails = workbookStatusRecords.map {
            SubscribeWorkbookDetail(
                workbookId = it.workbookId,
                isActiveSub = WorkBookStatus.fromStatus(it.isActiveSub),
                currentDay = it.currentDay,
                totalDay = it.totalDay,
                totalSubscriber = subscriptionWorkbookCountRecords[it.workbookId]?.toLong() ?: 0,
                subscription = subscriptionWorkbookSendStatusRecords[it.workbookId]?.let { record ->
                    Subscription(
                        time = record.sendTime,
                        dateTimeCode = record.sendDay
                    )
                } ?: throw IllegalStateException("${it.workbookId}'s subscription send status is null")
            )
        }

        return when (strategy) {
            SUBSCRIBE_WORKBOOK_STRATEGY.MAIN_CARD -> {
                val workbookSubscriptionCurrentArticleIdRecords = workbookStatusRecords.associate { record ->
                    val articleId = subscriptionArticleService.readArticleIdByWorkbookIdAndDay(
                        ReadArticleIdByWorkbookIdAndDayDto(record.workbookId, record.currentDay)
                    ) ?: throw NotFoundException("article.notfound.workbookIdAndCurrentDay")

                    record.workbookId to articleId
                }

                BrowseSubscribeWorkbooksUseCaseOut(
                    clazz = MainCardSubscribeWorkbookDetail::class.java,
                    workbooks = workbookDetails.map {
                        MainCardSubscribeWorkbookDetail(
                            workbookId = it.workbookId,
                            isActiveSub = it.isActiveSub,
                            currentDay = it.currentDay,
                            totalDay = it.totalDay,
                            totalSubscriber = it.totalSubscriber,
                            subscription = it.subscription,
                            articleInfo = objectMapper.writeValueAsString(
                                ArticleInfo(
                                    workbookSubscriptionCurrentArticleIdRecords[it.workbookId]
                                        ?: throw IllegalStateException("${it.workbookId}'s articleId is null")
                                )
                            )
                        )
                    }
                )
            }

            SUBSCRIBE_WORKBOOK_STRATEGY.MY_PAGE -> {
                val workbookTitleRecords = subscriptionWorkbookService.readAllWorkbookTitle(
                    ReadAllWorkbookTitleInDto(subscriptionWorkbookIds)
                )

                BrowseSubscribeWorkbooksUseCaseOut(
                    clazz = MyPageSubscribeWorkbookDetail::class.java,
                    workbooks = workbookDetails.map {
                        MyPageSubscribeWorkbookDetail(
                            workbookId = it.workbookId,
                            isActiveSub = it.isActiveSub,
                            currentDay = it.currentDay,
                            totalDay = it.totalDay,
                            totalSubscriber = it.totalSubscriber,
                            subscription = it.subscription,
                            workbookInfo = objectMapper.writeValueAsString(
                                WorkbookInfo(
                                    id = it.workbookId,
                                    title = workbookTitleRecords[it.workbookId]
                                        ?: throw IllegalStateException("${it.workbookId}'s title is null")
                                )
                            )
                        )
                    }
                )
            }
        }
    }
}