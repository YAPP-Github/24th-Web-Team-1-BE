package com.few.api.domain.workbook.service

import com.few.api.domain.workbook.service.dto.BrowseMemberSubscribeWorkbooksInDto
import com.few.api.domain.workbook.service.dto.BrowseMemberSubscribeWorkbooksOutDto
import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.query.SelectAllMemberWorkbookSubscriptionStatusNotConsiderDeletedAtQuery
import org.springframework.stereotype.Service

@Service
class WorkbookSubscribeService(
    private val subscriptionDao: SubscriptionDao,
) {

    fun browseMemberSubscribeWorkbooks(dto: BrowseMemberSubscribeWorkbooksInDto): List<BrowseMemberSubscribeWorkbooksOutDto> {
        return SelectAllMemberWorkbookSubscriptionStatusNotConsiderDeletedAtQuery(dto.memberId).let { it ->
            subscriptionDao.selectAllWorkbookSubscriptionStatus(it).map {
                BrowseMemberSubscribeWorkbooksOutDto(
                    workbookId = it.workbookId,
                    isActiveSub = it.isActiveSub,
                    currentDay = it.currentDay
                )
            }
        }
    }
}