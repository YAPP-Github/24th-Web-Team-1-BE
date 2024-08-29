package com.few.api.domain.article.service

import com.few.api.domain.article.service.dto.BrowseArticleProblemIdsInDto
import com.few.api.domain.article.service.dto.BrowseArticleProblemsOutDto
import com.few.api.exception.common.NotFoundException
import com.few.api.repo.dao.problem.ProblemDao
import com.few.api.repo.dao.problem.query.SelectProblemsByArticleIdQuery
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