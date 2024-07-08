package com.few.api.domain.problem.usecase

import com.few.api.domain.problem.usecase.`in`.BrowseProblemsUseCaseIn
import com.few.api.domain.problem.usecase.out.BrowseProblemsUseCaseOut
import com.few.api.exception.common.NotFoundException
import com.few.api.repo.dao.problem.ProblemDao
import com.few.api.repo.dao.problem.query.SelectProblemsByArticleIdQuery
import org.springframework.stereotype.Component

@Component
class BrowseProblemsUseCase(
    private val problemDao: ProblemDao
) {

    fun execute(useCaseIn: BrowseProblemsUseCaseIn): BrowseProblemsUseCaseOut {
        SelectProblemsByArticleIdQuery(useCaseIn.articleId).let { query: SelectProblemsByArticleIdQuery ->
            problemDao.selectProblemsByArticleId(query) ?: throw NotFoundException("problem.notfound.articleId")
        }.let {
            return BrowseProblemsUseCaseOut(it.problemIds)
        }
    }
}