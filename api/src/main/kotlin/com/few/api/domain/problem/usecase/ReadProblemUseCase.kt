package com.few.api.domain.problem.usecase

import com.few.api.domain.common.exception.NotFoundException
import com.few.api.domain.problem.repo.ProblemDao
import com.few.api.domain.problem.repo.query.SelectProblemQuery
import com.few.api.domain.problem.repo.support.ContentsJsonMapper
import com.few.api.domain.problem.usecase.dto.ReadProblemContentsUseCaseOutDetail
import com.few.api.domain.problem.usecase.dto.ReadProblemUseCaseIn
import com.few.api.domain.problem.usecase.dto.ReadProblemUseCaseOut
import org.springframework.stereotype.Component
import repo.jooq.DataSourceTransactional

@Component
class ReadProblemUseCase(
    private val problemDao: ProblemDao,
    private val contentsJsonMapper: ContentsJsonMapper,
) {
    @DataSourceTransactional(readOnly = true)
    fun execute(useCaseIn: ReadProblemUseCaseIn): ReadProblemUseCaseOut {
        val problemId = useCaseIn.problemId

        val record =
            problemDao.selectProblemContents(SelectProblemQuery(problemId))
                ?: throw NotFoundException("problem.notfound.id")

        val contents: List<ReadProblemContentsUseCaseOutDetail> =
            contentsJsonMapper.toObject(record.contents).contents.map {
                ReadProblemContentsUseCaseOutDetail(
                    number = it.number,
                    content = it.content,
                )
            }

        return ReadProblemUseCaseOut(
            id = record.id,
            title = record.title,
            contents = contents,
            articleId = record.articleId,
        )
    }
}