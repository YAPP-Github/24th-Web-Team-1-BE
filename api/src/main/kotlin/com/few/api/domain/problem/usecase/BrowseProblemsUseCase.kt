package com.few.api.domain.problem.usecase

import com.few.api.domain.problem.usecase.`in`.BrowseProblemsUseCaseIn
import com.few.api.domain.problem.usecase.out.BrowseProblemsUseCaseOut
import com.few.api.repo.dao.problem.ProblemDao
import com.few.api.repo.dao.problem.query.SelectProblemsByArticleIdQuery
import org.springframework.stereotype.Component

@Component
class BrowseProblemsUseCase(
    private val problemDao: ProblemDao
) {

    fun execute(useCaseIn: BrowseProblemsUseCaseIn): BrowseProblemsUseCaseOut {
        SelectProblemsByArticleIdQuery(useCaseIn.articleId).let { query: SelectProblemsByArticleIdQuery ->
            problemDao.selectProblemsByArticleId(query) ?: throw IllegalArgumentException("cannot find problems by articleId: ${query.articleId}") // todo 에러 표준화
        }.let {
            return BrowseProblemsUseCaseOut(it.problemIds)
        }
    }
}