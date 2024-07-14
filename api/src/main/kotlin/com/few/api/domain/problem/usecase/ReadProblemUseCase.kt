package com.few.api.domain.problem.usecase

import com.few.api.repo.dao.problem.ProblemDao
import com.few.api.repo.dao.problem.query.SelectProblemQuery
import com.few.api.domain.problem.usecase.dto.ReadProblemUseCaseIn
import com.few.api.domain.problem.usecase.dto.ReadProblemContentsUseCaseOutDetail
import com.few.api.domain.problem.usecase.dto.ReadProblemUseCaseOut
import com.few.api.exception.common.NotFoundException
import com.few.api.repo.dao.problem.support.ContentsJsonMapper
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ReadProblemUseCase(
    private val problemDao: ProblemDao,
    private val contentsJsonMapper: ContentsJsonMapper,
) {

    @Transactional(readOnly = true)
    fun execute(useCaseIn: ReadProblemUseCaseIn): ReadProblemUseCaseOut {
        val problemId = useCaseIn.problemId

        val record = problemDao.selectProblemContents(SelectProblemQuery(problemId))
            ?: throw NotFoundException("problem.notfound.id")

        val contents: List<ReadProblemContentsUseCaseOutDetail> = contentsJsonMapper.toObject(record.contents).contents.map {
            ReadProblemContentsUseCaseOutDetail(
                number = it.number,
                content = it.content
            )
        }

        return ReadProblemUseCaseOut(
            id = record.id,
            title = record.title,
            contents = contents
        )
    }
}