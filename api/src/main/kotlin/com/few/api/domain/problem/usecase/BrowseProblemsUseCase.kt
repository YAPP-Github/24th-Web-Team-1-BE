package com.few.api.domain.problem.usecase

import com.few.api.domain.common.exception.NotFoundException
import com.few.api.domain.problem.repo.ProblemDao
import com.few.api.domain.problem.repo.query.SelectProblemsByArticleIdQuery
import com.few.api.domain.problem.usecase.dto.BrowseProblemsUseCaseIn
import com.few.api.domain.problem.usecase.dto.BrowseProblemsUseCaseOut
import org.springframework.stereotype.Component
import repo.jooq.DataSourceTransactional

@Component
class BrowseProblemsUseCase(
    private val problemDao: ProblemDao,
) {
    @DataSourceTransactional(readOnly = true)
    fun execute(useCaseIn: BrowseProblemsUseCaseIn): BrowseProblemsUseCaseOut {
        problemDao
            .selectProblemsByArticleId(SelectProblemsByArticleIdQuery(useCaseIn.articleId))
            ?.let {
                return BrowseProblemsUseCaseOut(it.problemIds)
            } ?: throw NotFoundException("problem.notfound.articleId")
    }
}