package com.few.api.domain.problem.service

import com.few.api.domain.problem.service.dto.BrowseWorkbookIdAndProgressInDto
import com.few.api.domain.problem.service.dto.SubscriptionProgressOutDto
import com.few.api.exception.common.NotFoundException
import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.query.SelectSubscriptionSendStatusQuery
import org.springframework.stereotype.Component

@Component
class SubscriptionService(
    private val subscriptionDao: SubscriptionDao,
) {

    fun browseWorkbookIdAndProgress(inDto: BrowseWorkbookIdAndProgressInDto): List<SubscriptionProgressOutDto> {
        val subscriptionProgresses = subscriptionDao.selectWorkbookIdAndProgressByMember(
            SelectSubscriptionSendStatusQuery(inDto.memberId)
        ).takeIf { it.isNotEmpty() } ?: throw NotFoundException("subscribe.workbook.notexist")

        return subscriptionProgresses.map { SubscriptionProgressOutDto(it.workbookId, it.numOfReadArticle) }
    }
}