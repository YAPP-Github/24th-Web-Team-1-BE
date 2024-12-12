package com.few.api.domain.article.service

import com.few.api.domain.article.service.dto.BrowseArticleProblemIdsInDto
import com.few.api.domain.article.service.dto.BrowseArticleProblemsOutDto
import com.few.api.domain.common.exception.NotFoundException
import com.few.api.domain.problem.repo.ProblemDao
import com.few.api.domain.problem.repo.query.SelectProblemsByArticleIdQuery
import org.springframework.stereotype.Service

@Suppress("NAME_SHADOWING")
@Service
class BrowseArticleProblemsService(
    private val problemDao: ProblemDao,
) {

    fun execute(query: BrowseArticleProblemIdsInDto): BrowseArticleProblemsOutDto {
        return problemDao.selectProblemsByArticleId(SelectProblemsByArticleIdQuery(query.articleId))
            ?.let { BrowseArticleProblemsOutDto(it.problemIds) }
            ?: throw NotFoundException("problem.notfound.articleId")
    }
}