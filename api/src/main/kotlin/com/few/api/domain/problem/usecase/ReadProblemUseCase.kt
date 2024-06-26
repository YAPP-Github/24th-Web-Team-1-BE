package com.few.api.domain.problem.usecase

import com.few.api.repo.dao.problem.ProblemDao
import com.few.api.repo.dao.problem.query.SelectProblemQuery
import com.few.api.domain.problem.usecase.`in`.ReadProblemUseCaseIn
import com.few.api.domain.problem.usecase.out.ReadProblemContentsUseCaseOutDetail
import com.few.api.domain.problem.usecase.out.ReadProblemUseCaseOut
import com.few.api.repo.dao.problem.support.ContentsJsonMapper
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ReadProblemUseCase(
    private val problemDao: ProblemDao,
    private val contentsJsonMapper: ContentsJsonMapper
) {

    @Transactional(readOnly = true)
    fun execute(useCaseIn: ReadProblemUseCaseIn): ReadProblemUseCaseOut {
        val problemId = useCaseIn.problemId

        val record = problemDao.selectProblemContents(SelectProblemQuery(problemId))
            ?: throw RuntimeException("Problem with ID $problemId not found") // TODO: 에러 표준화

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