package com.few.api.domain.workbook.service

import com.few.api.domain.workbook.service.dto.BrowseMemberSubscribeWorkbooksInDto
import com.few.api.domain.workbook.service.dto.BrowseMemberSubscribeWorkbooksOutDto
import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.query.SelectAllMemberWorkbookActiveSubscription
import com.few.api.repo.dao.subscription.query.SelectAllMemberWorkbookInActiveSubscription
import org.springframework.stereotype.Service

@Service
class WorkbookSubscribeService(
    private val subscriptionDao: SubscriptionDao,
) {

    fun browseMemberSubscribeWorkbooks(dto: BrowseMemberSubscribeWorkbooksInDto): List<BrowseMemberSubscribeWorkbooksOutDto> {
        val inActiveSubscriptionRecords =
            SelectAllMemberWorkbookInActiveSubscription(dto.memberId).let {
                subscriptionDao.selectAllInActiveWorkbookSubscriptionStatus(it)
            }

        val activeSubscriptionRecords =
            SelectAllMemberWorkbookActiveSubscription(dto.memberId).let {
                subscriptionDao.selectAllActiveWorkbookSubscriptionStatus(it)
            }

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