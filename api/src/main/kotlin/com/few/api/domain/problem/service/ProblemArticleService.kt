package com.few.api.domain.problem.service

import com.few.api.domain.article.repo.ArticleDao
import com.few.api.domain.article.repo.query.SelectAritlceIdByWorkbookIdAndDayQuery
import com.few.api.domain.problem.service.dto.BrowseArticleIdInDto
import org.springframework.stereotype.Service

@Service
class ProblemArticleService(
    private val articleDao: ArticleDao,
) {
    fun browseArticleIdByWorkbookIdLimitDay(inDto: BrowseArticleIdInDto): List<Long> =
        articleDao
            .selectArticleIdsByWorkbookIdLimitDay(
                SelectAritlceIdByWorkbookIdAndDayQuery(
                    inDto.workbookId,
                    inDto.day,
                ),
            ).articleIds
}