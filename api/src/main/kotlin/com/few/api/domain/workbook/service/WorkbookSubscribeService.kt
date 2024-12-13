package com.few.api.domain.workbook.service

import com.few.api.domain.workbook.service.dto.BrowseMemberSubscribeWorkbooksInDto
import com.few.api.domain.workbook.service.dto.BrowseMemberSubscribeWorkbooksOutDto
import com.few.api.domain.subscription.repo.SubscriptionDao
import com.few.api.domain.subscription.repo.query.SelectAllMemberWorkbookActiveSubscriptionQuery
import com.few.api.domain.subscription.repo.query.SelectAllMemberWorkbookInActiveSubscriptionQuery
import org.springframework.stereotype.Service

@Service
class WorkbookSubscribeService(
    private val subscriptionDao: SubscriptionDao,
) {

    fun browseMemberSubscribeWorkbooks(dto: BrowseMemberSubscribeWorkbooksInDto): List<BrowseMemberSubscribeWorkbooksOutDto> {
        val inActiveSubscriptionRecords =
            subscriptionDao.selectAllInActiveWorkbookSubscriptionStatus(
                SelectAllMemberWorkbookInActiveSubscriptionQuery(dto.memberId)
            )

        val activeSubscriptionRecords = subscriptionDao.selectAllActiveWorkbookSubscriptionStatus(
            SelectAllMemberWorkbookActiveSubscriptionQuery(dto.memberId)
        )

        val subscriptionRecords = inActiveSubscriptionRecords + activeSubscriptionRecords

        return subscriptionRecords.map {
            BrowseMemberSubscribeWorkbooksOutDto(
                workbookId = it.workbookId,
                isActiveSub = it.isActiveSub,
                currentDay = it.currentDay
            )
        }
    }
}